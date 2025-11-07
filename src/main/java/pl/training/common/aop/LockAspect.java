package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static pl.training.common.aop.Lock.LockType.WRITE;

/**
 * Aspekt Spring AOP implementujący mechanizm blokowania dla synchronizacji dostępu do metod.
 * Zapewnia bezpieczeństwo wątkowe poprzez użycie blokad odczytu/zapisu.
 * 
 * @Aspect - deklaruje klasę jako aspekt AOP
 * @Component - rejestruje aspekt w kontenerze Spring
 * 
 * UWAGA: Obecna implementacja tworzy nową blokadę dla każdego wywołania,
 * co nie zapewnia rzeczywistej synchronizacji między wywołaniami.
 * W praktyce należałoby użyć współdzielonej instancji blokady.
 */
@Aspect
@Component
public class LockAspect {

    /**
     * @Around - porada wykonywana wokół metody docelowej
     * 
     * Wyrażenie pointcut "@annotation(lock)":
     * - Dopasowuje metody oznaczone adnotacją @Lock
     * - Parametr 'lock' w sygnaturze metody automatycznie wiąże się z adnotacją,
     *   dając dostęp do jej atrybutów (np. typu blokady)
     * 
     * ReentrantReadWriteLock - zaawansowany mechanizm blokowania Java:
     * - Pozwala na wielokrotny równoczesny odczyt (read lock)
     * - Zapewnia ekskluzywny dostęp przy zapisie (write lock)
     * - Jest "reentrant" - ten sam wątek może wielokrotnie uzyskać blokadę
     * 
     * @param joinPoint punkt złączenia reprezentujący wywołanie metody
     * @param lock adnotacja @Lock z metody, automatycznie wstrzyknięta przez Spring AOP
     */
    @Around("@annotation(lock)")
    public Object lock(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        // Tworzenie nowej blokady dla każdego wywołania - problem w obecnej implementacji!
        // Powinno być: współdzielona instancja per zasób/klasa
        var newLock = new ReentrantReadWriteLock();
        
        // Wybór typu blokady na podstawie adnotacji:
        // - WRITE: ekskluzywna blokada zapisu - tylko jeden wątek
        // - READ: współdzielona blokada odczytu - wiele wątków może czytać równocześnie
        var targetLock = lock.type() == WRITE ? newLock.writeLock() : newLock.readLock();
        
        // Zablokuj dostęp przed wykonaniem metody
        targetLock.lock();
        
        try {
            // Wykonaj oryginalną metodę w sekcji chronionej
            return joinPoint.proceed();
        } finally {
            // ZAWSZE odblokuj w bloku finally, nawet jeśli wystąpi wyjątek
            // To kluczowe dla uniknięcia deadlocków
            targetLock.unlock();
        }
    }

}

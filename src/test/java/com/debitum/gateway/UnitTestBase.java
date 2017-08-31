package com.debitum.gateway;

import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatcher;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;

@Category(UnitTestBase.class)
public abstract class UnitTestBase {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @SuppressWarnings("unchecked")
    public static class PredicateMatcher<T> extends ArgumentMatcher<T> {

        private final ArgumentMatcher<T> matcher;


        public PredicateMatcher(Predicate<T> predicate) {
            this.matcher = new ArgumentMatcher<T>() {
                public boolean matches(Object argument) {
                    return predicate.test((T) argument);
                }
            };
        }

        public static <T> T anyMatching(Predicate<T> predicate) {
            assertNotNull(predicate);
            return argThat(new PredicateMatcher<>(predicate));
        }

        public static <T> T anyMatching(Class<T> clazz, Predicate<T> predicate) {
            assertNotNull(clazz);
            assertNotNull(predicate);
            return argThat(new PredicateMatcher<>(predicate));
        }

        public static <E> List<E> anyListMatching(Predicate<List<E>> predicate) {
            assertNotNull(predicate);
            return argThat(new PredicateMatcher<>(predicate));
        }

        @Override
        public boolean matches(Object argument) {
            return matcher.matches(argument);
        }
    }
}
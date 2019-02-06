package ru.maklas.mengine;

/**
 * A Component for {@link Entity}.
 * The raw data for one aspect of the object, and how it interacts with the world.
 * It does nothing on itself and should not contain game logic. Only data. Game logic is executed in {@link SubscriptionSystem Systems}
 * <p>
 *     Examples of Components:
 *     <li>HealthComponent{health, maxHealth, isDead}</li>
 *     <li>DamageComponent{damage, target, source}</li>
 *     <li>RenderComponent{image}</li>
 * </p>
 */
public interface Component {

}

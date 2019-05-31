# MEngine
MEngine - is optimized Entity-Component-System-Event (ECSE) engine for 2d games.
Differences to Ashley:

Cons:
* Families are called `Groups` and can't consist of multiple Components. You can't get a Group of entities that have range of specified components. This feature was not found to be extremely useful, so sacrificed in favor of performance.
* No pooled Engine. Though can be organized with `EntityListener`

Pros:
* Faster. Adding, removing entities is faster since there is no need to traverse all Groups.
No need for PositionComponent as it's included in Entity.

`x`, `y`, `angle` - floats included in Entity for 2d positioning.

`layer` - integer, specifying layer of Entity for drawing order.

`id` - unique identifier for Entity. `Engine.findById()` can be used for search.

`type` - integer. Can be used as mask for any kind of purpose. Usual purpose - replacement for TypeComponent.

If you need this Engine to develop 3d game, feel free to fork and change Entity, giving it z coordinate or whatever.

* Built-in Event dispatching service. Main way of communication between User input, Systems and Entities. 
Allows to develop replaceable, pieces of code, easily manageable, Event-Systems that don't update, but rather handle specific tasks. See further for explanation.
* `Entity.class` can be extended. (You can create Entities just as easily without extending them), 
but this gives additional methods to extend: `addedToEngine(Engine engine)` and `removedFromEngine(Engine engine)`.
* `UpdatableEntity.class`. has update(dt) event to be extended. By adding `UpdatableEntitySystem` to the Engine, all UpdatableEntity instances will be updated each frame.

## EventDispatcher
Events is a way for User Input, Network, Systems and Entities to communicate inside of Engine.
Event dispatcher is built into engine. You can subscribe to any event from the outside like this:

```java
engine.subscribe(HitEvent.class, (event) -> {
            final Entity damageDealer = engine.getById(event.getDamageDealerId());
            final Entity target = engine.getById(event.getTargetId());
            final Component invincibilityC = target.get(invincibilityComponentMapper);
            if (invincibilityC == null){
                target.get(healthComponentMapper).hp -= event.getDamage();
                engine.dispatch(new DamageEvent(event.getTargetId(), event.getDamageDealerId(), event.getDamage()));
            }
        });
```
or inside of `SubscriptionSystem.onAddedToEngine(Engine engine)` function:

```java
@Override
    protected void onAddedToEngine(Engine engine) {
        subscribe(DamageEvent.class, (damageEvent) -> {
            if (damageEvent.getTarget().type == EntityType.PLAYER){
                System.out.println("Player got damaged!");
            }
        });
    }
```
_(Subscriptions which are created inside Systems are automatically unsubscribed when System is removed from Engine, so no need to do it by hand.)_

In this example, PhysicsSystem generates HitEvent. It dispatches into this subscription. 
Then it checks if target has Invincibility buff and in case it has not it applies damage and dispatches damage event. 
These events can follow each other. 
For example Damage event can generate Hit animation and sound or DeathEvent which can lead to a numerous others.

Example of Event chain: 

`CollisionEvent` (dispatched by PhysicsSystem when player touches lava) -> `DamageEvent` (by DamageSystem) -> `DeathEvent` (by HealthSystem) -> _sound of dead player_ (by SoundSystem), _death animation_ (by AnimationSystem) `GameOverEvent`.

If your game have multiple GameMods, all it takes is add or remove these subscriptions. 
Also if you suddenly need to add a quick feature, _for example adding damage numbers on screen when player gets hit by enemy_, 
all that is required is subscribing to `DamageEvent` and putting numbers on screen. No more no less. They're also a great help in managing game achievements.
These subscriptions can register how many times given task was performed. Damage applied, Enemies killed, Health healed, etc.

# MEngine
MEngine - is optimized Entity-Component-System engine for 2d games based on Ashley.
Differences:

Cons:
* Families are called Groups and can't consist of multiple Component choices. maxAmount of Families == Component classes.
* No PooledEngine.

Pros:
* Faster. Adding, removing entities is faster since there is no need to triverse all Families. This feature is not found to be useful, so sacrifised in favor of performance.
PositionComponents are not needed if you're developing 2d game.
x, y, angle - floats included in Entity as public fields.
layer - integer, specifying layer of Entity among others.
id - just a number for Entity to be searched.
type - integer. Can be used as mask for different purposes or other fast-identifying integer. Usual purpose - replace for TypeComponent.
All Entities hold non-dynamical Component array. To make sure you don't create extremely high array for each Entity, set Engine.TOTAL_COMPONENTS to the total Components classes you have in project. This might decrease memory usage a little.
If you need this Engine to develop 3d game, feel free to fork and change Entity, giving it z coordinate or whatever.
* Built-in Event dispatching service. Main way of communication between UserInput, Systems and Entities. Allows to develop replaceable, pieces of code, easily managable, Event-Systems that don't really update, but handle important events. See further for explanation.
* Entitiy.class made with assumption to be extended (You can create Entities just as easily without extending them), but this gives additional features:
  * Extendable methods: addedToEngine(Engine engine) and removedFromEngine(Engine engine);
  * Ability to subscribe to events on addition. No need to manualy unsubscribe after removal from engine.
* UpdatableEntity.class. has update(float dt) event to be extended. Control whether Entities should be updated before or after systems with setting Engine.UPDATE_ENTITIES_AFTER_ENGINE to true or false.
## EventDispatcher
Events is a way for User Input, Network, Systems and Entities to communicate inside of Engine.
Event dispatcher is built into engine. You can subscribe to any event from the outside like this:

```java
engine.subscribe(new Subscription<HitEvent>(HitEvent.class) {
            @Override
            public void receive(Signal<HitEvent> signal, HitEvent event) {
                final Entity damageDealer = engine.getById(event.getDamageDealerId());
                final Entity target = engine.getById(event.getTargetId());
                final Component invincibilityC = target.get(invincibilityComponentMapper);
                if (invincibilityC == null){
                    target.get(healthComponentMapper).hp -= event.getDamage();
                    engine.dispatch(new DamageEvent(event.getTargetId(), event.getDamageDealerId(), event.getDamage()));
                }
            }
        });
```
or inside of `Entity.addedToEngine(Engine engine)` function:

```java
@Override
    protected void addedToEngine(final Engine engine) {
        subscribe(new Subscription<DamageEvent>(DamageEvent.class) {
            @Override
            public void receive(Signal<DamageEvent> signal, DamageEvent damageEvent) {
                if (damageEvent.getTarget() == EntityPlayer.this){
                    System.out.println("Player got damaged");
                }
            }
        });
    }
```
Here, in first example, PhysicsSystem generates HitEvent. It dispatches into this subscription. Here it checks if target has Invincibility buff and in case it has not it applies damage and dispatches damage event. These events can go along way. For example Damage event can generate Hit animation and sound or DeathEvent which can lead to a numerous others. If your game have multiple GameMods, all it takes is add or remove these subscriptions. This still might look like a lot of boilerplate code, but IDE makes this fast and It pays you later when codebase grows
Subscriptions which are created inside Entity are automaticaly unsubscribed when Entity is removed from Engine, so no need to to it by hand.

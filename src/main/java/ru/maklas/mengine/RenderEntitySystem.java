package ru.maklas.mengine;

/**
 * Abstract EntitySystem that has render() method instead of update().
 * This system should be used for rendering on screen
 * If System implements this class, it's not called during {@link Engine#update(float)}
 * But called during {@link Engine#render()}
 */
public abstract class RenderEntitySystem extends EntitySystem {

    private boolean valid = false;
    private boolean alwaysInvalidate = false;

    public RenderEntitySystem() {
        super();
    }

    @Override
    public final void update(float dt) {
        render();
    }

    /** Called every frame during {@link Engine#render()} **/
    public abstract void render();

    /** Invalidates rendering order which will force to re-layout all Entities based on their layer **/
    public final void invalidate(){
        this.valid = false;
    }

    /** Sets Entity layout to valid. Meaning that Entities are rendered in the correct order based on their layer **/
    public final void setValid(){
        this.valid = true;
    }

    /** Whether or not this system always invalidated before render begins **/
    public final boolean isAlwaysInvalidate() {
        return alwaysInvalidate;
    }

    /**
     * If true, Rendering order will always be invalid before rendering starts,
     * forcing to do re-layout of Entities every frame.
     * If you want to control layout of Entities and optimize it yourself, set this to false.
     */
    public final void setAlwaysInvalidate(boolean alwaysInvalidate) {
        this.alwaysInvalidate = alwaysInvalidate;
    }

    /** Whether or not this system's rendering order is valid. If not, it will require re-layout before rendering **/
    public final boolean isValid(){
        return valid && !alwaysInvalidate;
    }


}

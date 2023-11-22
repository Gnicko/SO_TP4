package gomez.tolosa;

public enum Ubicacion {
    OESTE,ESTE;
    public static Ubicacion cambiarUbicacion(Ubicacion ubicacionActual) {
        return (ubicacionActual == Ubicacion.OESTE) ? Ubicacion.ESTE : Ubicacion.OESTE;
    }
}


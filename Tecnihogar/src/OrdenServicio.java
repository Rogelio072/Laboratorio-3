import java.util.*;

enum EstadoOrden { PENDIENTE, ASIGNADA, EN_ATENCION, COMPLETADA, CANCELADA }
enum EstadoJornada { EN_PREPARACION, EN_ATENCION, CERRADA }

abstract class OrdenServicio {
    protected String codigo;
    protected String cliente;
    protected String direccion;
    protected double horasEstimadas;
    protected String zona; // "Metropolitana" o "Periferica"
    protected EstadoOrden estado;
    protected int visitasRealizadas;

    public OrdenServicio(String codigo, String cliente, String direccion, double horasEstimadas, String zona) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
    }

    public abstract double calcularCosto();
    public abstract int getTiempoAtencionDias();
    public abstract int getLimiteVisitas();
    public abstract boolean requiereCertificacionRefrigeracion();

    public String getCodigo() { return codigo; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public int getVisitasRealizadas() { return visitasRealizadas; }
    public void incrementarVisitas() { this.visitasRealizadas++; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public String getZona() { return zona; }
    public String getCategoriaNombre() { return this.getClass().getSimpleName(); }
}
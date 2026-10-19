import java.util.*;

class Cuadrilla {
    private String codigo;
    private double capacidadHoras;
    private boolean certificadaRefrigeracion;
    private boolean ocupada;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        this.codigo = codigo;
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
        this.ocupada = false;
    }

    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificadaRefrigeracion() { return certificadaRefrigeracion; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }
}

class Jornada {
    private Cuadrilla cuadrilla;
    private List<OrdenServicio> ordenesAsignadas;
    private EstadoJornada estado;

    public Jornada(Cuadrilla cuadrilla) {
        if (cuadrilla.isOcupada()) {
            throw new IllegalStateException("La cuadrilla ya tiene una jornada sin cerrar.");
        }
        this.cuadrilla = cuadrilla;
        this.cuadrilla.setOcupada(true);
        this.ordenesAsignadas = new ArrayList<>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    public void asignarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden asignar órdenes en jornada en preparación.");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("La orden no está disponible (estado pendiente requerido).");
        }
        double horasActuales = ordenesAsignadas.stream().mapToDouble(OrdenServicio::getHorasEstimadas).sum();
        if ((horasActuales + orden.getHorasEstimadas()) > cuadrilla.getCapacidadHoras()) {
            throw new IllegalArgumentException("Supera la capacidad máxima de horas de la cuadrilla.");
        }
        if (orden.requiereCertificacionRefrigeracion() && !cuadrilla.isCertificadaRefrigeracion()) {
            throw new IllegalArgumentException("Esta orden requiere cuadrilla certificada en refrigeración.");
        }
        
        ordenesAsignadas.add(orden);
        orden.setEstado(EstadoOrden.ASIGNADA);
    }

    public void retirarOrden(OrdenServicio orden) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("Solo se pueden retirar órdenes en jornada en preparación.");
        }
        if (ordenesAsignadas.remove(orden)) {
            orden.setEstado(EstadoOrden.PENDIENTE);
        } else {
            throw new IllegalArgumentException("La orden no se encuentra en esta jornada.");
        }
    }

    public void iniciarJornada(GestorIngresos gestor) {
        if (estado != EstadoJornada.EN_PREPARACION) {
            throw new IllegalStateException("La jornada ya fue iniciada o cerrada.");
        }
        if (ordenesAsignadas.isEmpty()) {
            throw new IllegalStateException("No puede iniciar una jornada vacía.");
        }
        this.estado = EstadoJornada.EN_ATENCION;
        for (OrdenServicio o : ordenesAsignadas) {
            o.setEstado(EstadoOrden.EN_ATENCION);
            // Registrar cobro inicial de la orden
            double costoOrden = o.calcularCosto();
            gestor.registrarIngresoRegistro(o.getCategoriaNombre(), costoOrden);

            // Verificar si hay visitas adicionales acumuladas de intentos previos
            if (o.getVisitasRealizadas() > 0) {
                double cargoVisitasExtra = o.getVisitasRealizadas() * 40.0;
                gestor.registrarIngresoVisitasAdicionales(cargoVisitasExtra);
            }
        }
    }

    public void registrarResultadoOrden(String codigoOrden, boolean exitosa, GestorIngresos gestor) {
        if (estado != EstadoJornada.EN_ATENCION) {
            throw new IllegalStateException("La jornada no está en atención.");
        }
        OrdenServicio ordenEncontrada = null;
        for (OrdenServicio o : ordenesAsignadas) {
            if (o.getCodigo().equalsIgnoreCase(codigoOrden)) {
                ordenEncontrada = o;
                break;
            }
        }
        if (ordenEncontrada == null) {
            throw new IllegalArgumentException("La orden no pertenece a esta jornada.");
        }

        ordenEncontrada.incrementarVisitas();
        if (exitosa) {
            ordenEncontrada.setEstado(EstadoOrden.COMPLETADA);
        } else {
            // Visita fallida
            if (ordenEncontrada.getVisitasRealizadas() >= ordenEncontrada.getLimiteVisitas()) {
                ordenEncontrada.setEstado(EstadoOrden.CANCELADA);
            } else {
                ordenEncontrada.setEstado(EstadoOrden.PENDIENTE);
            }
        }

        // Verificar si todas las órdenes ya tienen resultado para cerrar jornada
        boolean todasAtendidas = ordenesAsignadas.stream().allMatch(o -> o.getEstado() == EstadoOrden.COMPLETADA || o.getEstado() == EstadoOrden.CANCELADA);
        if (todasAtendidas) {
            this.estado = EstadoJornada.CERRADA;
            this.cuadrilla.setOcupada(false);
        }
    }

    public EstadoJornada getEstado() { return estado; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public List<OrdenServicio> getOrdenesAsignadas() { return ordenesAsignadas; }
    public double getCapacidadDisponible() {
        double usadas = ordenesAsignadas.stream().mapToDouble(OrdenServicio::getHorasEstimadas).sum();
        return cuadrilla.getCapacidadHoras() - usadas;
    }
}

class GestorIngresos {
    private double ingresoRegistro = 0.0;
    private double ingresoVisitasAdicionales = 0.0;
    private Map<String, Double> ingresosPorCategoria = new HashMap<>();

    public void registrarIngresoRegistro(String categoria, double monto) {
        ingresoRegistro += monto;
        ingresosPorCategoria.put(categoria, ingresosPorCategoria.getOrDefault(categoria, 0.0) + monto);
    }

    public void registrarIngresoVisitasAdicionales(double monto) {
        ingresoVisitasAdicionales += monto;
    }

    public double getIngresoRegistro() { return ingresoRegistro; }
    public double getIngresoVisitasAdicionales() { return ingresoVisitasAdicionales; }
    public Map<String, Double> getIngresosPorCategoria() { return ingresosPorCategoria; }
}
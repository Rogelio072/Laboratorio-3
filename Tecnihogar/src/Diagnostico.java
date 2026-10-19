class Diagnostico extends OrdenServicio {
    public Diagnostico(String codigo, String cliente, String direccion, double horasEstimadas, String zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > 1.5) {
            throw new IllegalArgumentException("Un diagnóstico no puede superar las 1.5 horas.");
        }
    }

    @Override
    public double calcularCosto() {
        return zona.equalsIgnoreCase("Metropolitana") ? 150.0 : 225.0;
    }

    @Override
    public int getTiempoAtencionDias() {
        return zona.equalsIgnoreCase("Metropolitana") ? 1 : 2;
    }

    @Override
    public int getLimiteVisitas() { return 3; }

    @Override
    public boolean requiereCertificacionRefrigeracion() { return false; }
}

class Instalacion extends OrdenServicio {
    protected int puntos;

    public Instalacion(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (puntos <= 0) throw new IllegalArgumentException("La cantidad de puntos debe ser un entero positivo.");
        this.puntos = puntos;
    }

    @Override
    public double calcularCosto() {
        double tarifaBase = zona.equalsIgnoreCase("Metropolitana") ? 100.0 : 175.0;
        double horasPorPuntos = puntos * 0.75;
        double horasFacturables = Math.ceil(Math.max(horasEstimadas, horasPorPuntos));
        return tarifaBase + (90.0 * horasFacturables);
    }

    @Override
    public int getTiempoAtencionDias() {
        return zona.equalsIgnoreCase("Metropolitana") ? 3 : 5;
    }

    @Override
    public int getLimiteVisitas() { return 3; }

    @Override
    public boolean requiereCertificacionRefrigeracion() { return false; }
}

class InstalacionElectricaCertificada extends Instalacion {
    private double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int puntos, double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        this.valorMateriales = valorMateriales;
    }

    @Override
    public double calcularCosto() {
        double costoBaseInstalacion = super.calcularCosto();
        double costoCertificado = 120.0;
        double garantiaMateriales = valorMateriales * 0.05;
        return costoBaseInstalacion + costoCertificado + garantiaMateriales;
    }

    @Override
    public int getTiempoAtencionDias() {
        return super.getTiempoAtencionDias() + 2;
    }

    @Override
    public int getLimiteVisitas() { return 2; }
}

class MantenimientoAireAcondicionado extends OrdenServicio {
    private int btu;

    public MantenimientoAireAcondicionado(String codigo, String cliente, String direccion, double horasEstimadas, String zona, int btu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (!zona.equalsIgnoreCase("Metropolitana")) {
            throw new IllegalArgumentException("Solo se aceptan mantenimientos de aire acondicionado en zona metropolitana.");
        }
        if (btu < 9000 || btu > 60000) {
            throw new IllegalArgumentException("La capacidad en BTU debe estar entre 9,000 y 60,000.");
        }
        this.btu = btu;
    }

    @Override
    public double calcularCosto() {
        double tarifaBase = 100.0; // Metropolitana
        return tarifaBase + (110.0 * horasEstimadas) + 80.0;
    }

    @Override
    public int getTiempoAtencionDias() { return 2; }

    @Override
    public int getLimiteVisitas() { return 1; }

    @Override
    public boolean requiereCertificacionRefrigeracion() { return true; }
}
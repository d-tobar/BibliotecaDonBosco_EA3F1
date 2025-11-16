package sv.edu.udb.biblioteca;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Prestamo {
    private int id;
    private String usuarioNombre;
    private String tituloMaterial;
    private LocalDateTime fechaPrestamo;
    private LocalDate fechaLimite;
    private BigDecimal mora;
    private String estado;

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getTituloMaterial() { return tituloMaterial; }
    public void setTituloMaterial(String tituloMaterial) { this.tituloMaterial = tituloMaterial; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }
    public BigDecimal getMora() { return mora; }
    public void setMora(BigDecimal mora) { this.mora = mora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

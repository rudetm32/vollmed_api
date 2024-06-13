package med.voll.api.domain.medico;

import med.voll.api.domain.direccion.DatosDireccion;

import jakarta.validation.constraints.NotNull;


public record DatosActualizarMedico(
        @NotNull
        Long id,
        String nombre,
        String documento,
        DatosDireccion direccion
) {
}

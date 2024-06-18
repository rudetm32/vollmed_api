package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import med.voll.api.domain.medico.DatosListadoMedico;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.net.URI;


@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaMedico> registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico, UriComponentsBuilder uriComponentsBuilder){
       Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
       DatosRespuestaMedico datosRespuestaMedico =  convertirDatosRespuesta(medico);
       URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
       return ResponseEntity.created(url).body(datosRespuestaMedico);
    }


    @GetMapping
    public ResponseEntity<Page<DatosListadoMedico>> listaMedicos(@PageableDefault(size = 10, sort = "nombre") Pageable paginacion) {
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DatosRespuestaMedico> actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico){
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);
        return ResponseEntity.ok(convertirDatosRespuesta(medico));
    }

    // borrado logico
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity borrarMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById((id));
        medico.desactivarMedico();
        System.out.println(medico.getActivo());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        var datosMedico = convertirDatosRespuesta(medico);
        return ResponseEntity.ok(datosMedico);
    }

    private DatosRespuestaMedico convertirDatosRespuesta(Medico medico) {
        return new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
                medico.getEmail(), medico.getTelefono(),
                medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(),
                        medico.getDireccion().getDistrito(),
                        medico.getDireccion().getCiudad(),
                        medico.getDireccion().getComplemento(),
                        medico.getDireccion().getNumero()));
    }
}

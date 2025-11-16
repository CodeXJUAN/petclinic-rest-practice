package org.springframework.samples.petclinic.service.clinicService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.*;
import org.springframework.samples.petclinic.service.ClinicServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test class for ClinicServiceImpl - Fase 3 Coverage Tests with Mockito
 * 
 * Estos tests usan Mockito para aislar la lógica de negocio del servicio
 * y validar interacciones con los repositorios.
 * 
 * @author Juan Manuel López    
 */
@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private VetRepository vetRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private PetTypeRepository petTypeRepository;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    private Owner owner;
    private Pet pet;
    private PetType petType;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        petType = new PetType();
        petType.setId(1);
        petType.setName("dog");

        pet = new Pet();
        pet.setId(1);
        pet.setName("Max");
        pet.setBirthDate(LocalDate.now());
        pet.setType(petType);
        pet.setOwner(owner);
    }

    /**
     * TEST 1: Verifica que savePet() busca el PetType antes de guardar
     * 
     * Por qué aporta valor:
     * - Valida que el servicio enriquece el Pet con el PetType completo
     * - Asegura que se hace la búsqueda del tipo antes de guardar
     * - Cubre una ramificación del método savePet() poco testeada
     * - USA MOCKITO para verificar las interacciones con los repositorios
     */
    @Test
    void testSavePetShouldFetchPetTypeBeforeSaving() {
        when(petTypeRepository.findById(1)).thenReturn(petType);

        clinicService.savePet(pet);

        verify(petTypeRepository, times(1)).findById(1);
        verify(petRepository, times(1)).save(pet);
        assertThat(pet.getType()).isNotNull();
        assertThat(pet.getType().getName()).isEqualTo("dog");
    }

    /**
     * TEST 2: Verifica que findAllOwners() devuelve todos los owners del repositorio
     * 
     * Por qué aporta valor:
     * - Valida que el servicio delega correctamente en el repositorio
     * - Asegura que se devuelve la colección completa sin filtros
     * - Cubre el método findAllOwners() que puede tener baja cobertura
     * - USA MOCKITO para simular el comportamiento del repositorio
     */
    @Test
    void testFindAllOwnersShouldReturnAllOwnersFromRepository() {
        Owner owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("John");

        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Jane");

        Collection<Owner> expectedOwners = Arrays.asList(owner1, owner2);
        when(ownerRepository.findAll()).thenReturn(expectedOwners);

        Collection<Owner> actualOwners = clinicService.findAllOwners();

        verify(ownerRepository, times(1)).findAll();
        assertThat(actualOwners).hasSize(2);
        assertThat(actualOwners).contains(owner1, owner2);
    }

    /**
     * TEST 3: Verifica que deleteOwner() invoca el método delete del repositorio
     * 
     * Por qué aporta valor:
     * - Valida que el servicio delega correctamente la eliminación
     * - Asegura que se llama al repositorio con el owner correcto
     * - Cubre el método deleteOwner() que puede tener poca cobertura
     * - USA MOCKITO para verificar que se llama al método delete
     */
    @Test
    void testDeleteOwnerShouldInvokeRepositoryDelete() {
        clinicService.deleteOwner(owner);

        verify(ownerRepository, times(1)).delete(owner);
        verifyNoMoreInteractions(ownerRepository);
    }

    /**
     * TEST 4 (BONUS): Verifica findOwnerById maneja correctamente owner no encontrado
     * 
     * Por qué aporta valor:
     * - Valida manejo de casos negativos (owner no existe)
     * - Asegura que no se lanzan excepciones inesperadas
     * - Cubre ramificación de error del método findOwnerById
     * - USA MOCKITO para simular un repositorio que no encuentra el owner
     */
    @Test
    void testFindOwnerByIdShouldReturnNullWhenOwnerNotFound() {

        when(ownerRepository.findById(999)).thenReturn(null);

        Owner result = clinicService.findOwnerById(999);

        verify(ownerRepository, times(1)).findById(999);
        assertThat(result).isNull();
    }
}
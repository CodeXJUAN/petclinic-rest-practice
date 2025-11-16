package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Owner entity - Fase 3 Coverage Tests
 * 
 * Estos tests cubren métodos de negocio poco testeados:
 * - getPet(String name): buscar mascota por nombre
 * - getPet(Integer petId): buscar mascota por ID
 * - addPet(Pet pet): añadir mascota y establecer relación bidireccional
 * 
 * @author Juan Manuel López
 */
class OwnerTest {

    private Owner owner;
    private Pet pet1;
    private Pet pet2;
    private PetType petType;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("5551234567");

        petType = new PetType();
        petType.setId(1);
        petType.setName("dog");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Max");
        pet1.setBirthDate(LocalDate.of(2020, 5, 15));
        pet1.setType(petType);

        pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Buddy");
        pet2.setBirthDate(LocalDate.of(2021, 3, 10));
        pet2.setType(petType);
    }

    /**
     * TEST 1: Verifica que addPet() establece correctamente la relación bidireccional
     * 
     * Por qué aporta valor:
     * - Valida que la relación Owner-Pet se establece correctamente
     * - Asegura que pet.getOwner() devuelve el owner correcto después de addPet()
     * - Cubre una rama del método addPet() que no estaba testeada
     */
    @Test
    void testAddPetShouldSetOwnerRelationship() {
        owner.addPet(pet1);

        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets().get(0).getName()).isEqualTo("Max");
        assertThat(pet1.getOwner()).isEqualTo(owner);
        assertThat(pet1.getOwner().getId()).isEqualTo(1);
    }

    /**
     * TEST 2: Verifica que getPet(String name) encuentra correctamente una mascota por nombre
     * 
     * Por qué aporta valor:
     * - Cubre el método getPet(String) que tenía 0% de cobertura
     * - Valida búsqueda case-insensitive (importante para UX)
     * - Prueba escenario con múltiples mascotas
     */
    @Test
    void testGetPetByNameShouldFindPetCaseInsensitive() {
        owner.addPet(pet1);
        owner.addPet(pet2);

        Pet foundPet1 = owner.getPet("max");      
        Pet foundPet2 = owner.getPet("MAX");     
        Pet foundPet3 = owner.getPet("Buddy");    

        assertThat(foundPet1).isNotNull();
        assertThat(foundPet1.getName()).isEqualTo("Max");
        assertThat(foundPet2).isNotNull();
        assertThat(foundPet2.getId()).isEqualTo(1);
        assertThat(foundPet3).isNotNull();
        assertThat(foundPet3.getName()).isEqualTo("Buddy");
    }

    /**
     * TEST 3: Verifica que getPet(String name) devuelve null cuando no encuentra la mascota
     * 
     * Por qué aporta valor:
     * - Cubre el caso negativo (mascota no encontrada)
     * - Valida que no se lanzan excepciones con nombres inexistentes
     * - Asegura comportamiento robusto ante datos inválidos
     */
    @Test
    void testGetPetByNameShouldReturnNullWhenPetNotFound() {
        owner.addPet(pet1);
        
        Pet foundPet = owner.getPet("NonExistentPet");
        
        assertThat(foundPet).isNull();
    }

    /**
     * TEST 4: Verifica getPet(Integer petId) encuentra mascota por ID
     * 
     * Por qué aporta valor:
     * - Cubre el método getPet(Integer) que tenía baja cobertura
     * - Valida búsqueda por ID (alternativa a búsqueda por nombre)
     * - Prueba con múltiples mascotas
     */
    @Test
    void testGetPetByIdShouldFindCorrectPet() {
        owner.addPet(pet1);
        owner.addPet(pet2);

        Pet foundPet1 = owner.getPet(1);
        Pet foundPet2 = owner.getPet(2);
        Pet notFound = owner.getPet(999);

        assertThat(foundPet1).isNotNull();
        assertThat(foundPet1.getName()).isEqualTo("Max");
        assertThat(foundPet2).isNotNull();
        assertThat(foundPet2.getName()).isEqualTo("Buddy");
        assertThat(notFound).isNull();
    }
}
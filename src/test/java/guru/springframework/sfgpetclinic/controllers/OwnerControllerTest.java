package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    @Mock
    BindingResult bindingResult;

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController controller;

    @Test
    void processCreationFormFailed() {
        // Given
        given(bindingResult.hasErrors()).willReturn(true);
        Owner owner = mock(Owner.class);

        // When
        String view = controller.processCreationForm(owner, bindingResult);

        // Then
        then(ownerService).should(times(0)).save(any());
        assertEquals("owners/createOrUpdateOwnerForm", view);
    }

    @Test
    void processCreationFormSuccess() {
        // Given
        given(bindingResult.hasErrors()).willReturn(false);
        Owner owner = mock(Owner.class);
        Owner savedOwner = mock(Owner.class);
        given(savedOwner.getId()).willReturn(5L);
        given(ownerService.save(owner)).willReturn(savedOwner);

        // When
        String view = controller.processCreationForm(owner, bindingResult);

        // Then
        then(ownerService).should(times(1)).save(any());
        assertEquals("redirect:/owners/5", view);
    }
}
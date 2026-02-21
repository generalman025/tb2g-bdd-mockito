package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    @Mock
    BindingResult bindingResult;

    @Mock
    OwnerService ownerService;

    @Mock
    Model model;

    @InjectMocks
    OwnerController controller;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> owners = new ArrayList<>();

                    String name = invocation.getArgument(0);

                    if(name.equals("%Buck%")){
                        owners.add(new Owner(1L, "Joe", "Buck"));
                        return owners;
                    } else if(name.equals("%DontFindMe")){
                        return owners;
                    } else if(name.equals("%FindMe%")){
                        owners.add(new Owner(1L, "Joe", "Buck"));
                        owners.add(new Owner(2L, "Joe2", "Buck2"));
                        return owners;
                    }

                    throw new RuntimeException("Invalid argument");
                });
    }

    @Test
    void processFindFordWildcardString(){
        // Given
        Owner owner = new Owner(1L, "Joe", "FindMe");
        InOrder inOrder = Mockito.inOrder(ownerService, model);

        // When
        String viewName = controller.processFindForm(owner, bindingResult, model);

        // Then
        assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        // InOrder asserts
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model).addAttribute(anyString(), anyList());
        verifyNoMoreInteractions(ownerService, model);
    }

    @Test
    void processFindFordWildcardStringAnnotation(){
        // Given
        Owner owner = new Owner(1L, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        // When
        String viewName = controller.processFindForm(owner, bindingResult, null);

        // Then
        assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
    }

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
package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int DEFAULT_PAGE_NUMBER = 0;
    @Captor
    ArgumentCaptor<PageRequest> captor;

    @Captor
    ArgumentCaptor<UUID> customerIdCaptor;

    @Captor
    ArgumentCaptor<UUID> oderIdCaptor;

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;
    private UUID customerId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        List<BeerOrderDto> beerList = new ArrayList<>();
        beerList.add(BeerOrderDto.builder().build());
        BeerOrderPagedList beerOrderPagedList = new BeerOrderPagedList(beerList);

        when(beerOrderService.listOrders(any(), captor.capture())).thenReturn(beerOrderPagedList);
        when(beerOrderService.getOrderById(customerIdCaptor.capture(), oderIdCaptor.capture())).thenReturn(BeerOrderDto.builder().build());

        customerId = UUID.randomUUID();
        orderId = UUID.randomUUID();
    }

    @Test
    void listOrdersDefaults() throws Exception {

        mockMvc.perform(get("/api/v1/customers/" +
                customerId + "/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        PageRequest value = captor.getValue();
        assertEquals(DEFAULT_PAGE_SIZE, value.getPageSize());
        assertEquals(DEFAULT_PAGE_NUMBER, value.getPageNumber());
        verify(beerOrderService).listOrders(eq(customerId), any());

    }

    @Test
    void listOrdersPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/customers/" +
                customerId + "/orders").param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        PageRequest value = captor.getValue();
        assertEquals(5, value.getPageSize());
        assertEquals(DEFAULT_PAGE_NUMBER, value.getPageNumber());
        verify(beerOrderService).listOrders(eq(customerId), any());
    }

    @Test
    void listOrdersPageNumber() throws Exception {

        mockMvc.perform(get("/api/v1/customers/" +
                customerId + "/orders").param("pageNumber", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        PageRequest value = captor.getValue();
        assertEquals(DEFAULT_PAGE_SIZE, value.getPageSize());
        assertEquals(5, value.getPageNumber());
        verify(beerOrderService).listOrders(eq(customerId), any());
    }


    @Test
    void getOrder() throws Exception {

        mockMvc.perform(get("/api/v1/customers/" +
                customerId + "/orders/" +
                orderId))
                .andExpect(status().isOk());

        assertEquals(customerId, customerIdCaptor.getValue());
        assertEquals(orderId, oderIdCaptor.getValue());
        verify(beerOrderService).getOrderById(eq(customerId), eq(orderId));
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }
}
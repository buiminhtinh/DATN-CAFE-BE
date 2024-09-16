package cafe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cafe.dto.OrderDto;
import cafe.dto.OrderdetailDto;
import cafe.entity.Order;
import cafe.entity.OrderDetail;
import cafe.modal.OrderResponse;
import cafe.service.MapValidationErrorService;
import cafe.service.OrderDetailService;
import cafe.service.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderDetailService detailService;

	@Autowired
	private MapValidationErrorService mapValidationErrorService;

	@GetMapping
	public ResponseEntity<?> getAllOrder() {
		return new ResponseEntity<>(orderService.findAll().stream().map(OrderResponse::convert).toList(),
				HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<?> getOrder(@PathVariable("id") Long id) {
		return orderService.findById(id).map(order -> ResponseEntity.ok(OrderResponse.convert(order))) // Chuyển đổi Order thành OrderResponse
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new OrderResponse())); // Trả về một OrderResponse rỗng hoặc null
	}

	@PostMapping
	public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto dto, BindingResult result) {
		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationField(result);

		if (responseEntity != null) {
			return responseEntity;
		}

		// Kiểm tra và khởi tạo danh sách rỗng nếu cần
		if (dto.getOrderdetails() == null) {
			dto.setOrderdetails(new ArrayList<>());
		}

		// Tạo Order từ OrderDto
		Order order = orderService.createOrder(dto);

		// Chuyển đổi Order thành OrderDto để trả về cho client
		OrderDto respDto = new OrderDto();
		respDto.setActive(order.getActive());
		respDto.setCashier(order.getCashier());
		respDto.setCreatedtime(order.getCreatedtime());
		respDto.setCustomer(order.getCustomer());
		respDto.setFulladdresstext(order.getFulladdresstext());

		// Chuyển đổi OrderDetail thành OrderdetailDto
		List<OrderdetailDto> orderDetailDtos = order.getOrderdetails().stream().map(this::convertToDto)
				.collect(Collectors.toList());
		respDto.setOrderdetails(orderDetailDtos);

		respDto.setPaymentmethod(order.getPaymentmethod());
		respDto.setShippingfee(order.getShippingfee());
		respDto.setStatus(order.getStatus());
		respDto.setTotalamount(order.getTotalamount());

		return new ResponseEntity<>(respDto, HttpStatus.CREATED);
	}

	private OrderdetailDto convertToDto(OrderDetail orderDetail) {
		OrderdetailDto dto = new OrderdetailDto();
		dto.setProductvariant(orderDetail.getProductvariant());
		dto.setQuantity(orderDetail.getQuantity());
		dto.setMomentprice(orderDetail.getMomentprice());
		dto.setNote(orderDetail.getNote());
		return dto;
	}

}

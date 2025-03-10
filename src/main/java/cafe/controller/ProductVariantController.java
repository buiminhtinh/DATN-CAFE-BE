package cafe.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cafe.dto.CategoryDto;
import cafe.dto.ProductDto;
import cafe.dto.ProductVariantDto;
import cafe.entity.Category;
import cafe.entity.Product;
import cafe.entity.ProductVariant;
import cafe.service.MapValidationErrorService;
import cafe.service.ProductVariantService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productvariants")
@CrossOrigin
public class ProductVariantController {

	@Autowired
	ProductVariantService productVariantService;

	@Autowired
	MapValidationErrorService mapValidationErrorService;

	@PostMapping
	public ResponseEntity<?> createProductVariant(@Valid @RequestBody ProductVariantDto dto, BindingResult result) {

		ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationField(result);
		if (responseEntity != null) {
			return responseEntity;
		}

		ProductVariant entity = new ProductVariant();

		ProductVariantDto variantDto = new ProductVariantDto();
		BeanUtils.copyProperties(entity, variantDto, "size");

		entity = productVariantService.save(dto);

		dto.setId(entity.getId());
		return new ResponseEntity<>(entity, HttpStatus.CREATED);

	}

    //Test code
	// cập nhật
	@PatchMapping("/{id}")
	public ResponseEntity<?> UpdateProductVariant(@PathVariable Long id, @RequestBody ProductVariantDto dto) {
		
		ProductVariant variant = productVariantService.update(id, dto);
		
		return new ResponseEntity<>(variant, HttpStatus.CREATED);
	}
	
	@PatchMapping("/{id}/toggle-active")
	public ResponseEntity<?> toggleActive(@PathVariable Long id){
		ProductVariant variant = productVariantService.toggleActive(id);
		
		ProductVariantDto response = new ProductVariantDto();
		BeanUtils.copyProperties(variant, response);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<?> getProductVariants() {
		return new ResponseEntity<>(productVariantService.findAll(), HttpStatus.OK);
	}

	// cái này để phân trang
	@GetMapping("/page")
	public ResponseEntity<?> getProducts(
			@PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return new ResponseEntity<>(productVariantService.findAll(pageable), HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<?> getCategories(@PathVariable("id") Long id) {
		return new ResponseEntity<>(productVariantService.findById(id), HttpStatus.OK);
	}
	
	@GetMapping("/find")
    public ResponseEntity<?> searchProductVariants(@RequestParam("productName") String productName) {
        return new ResponseEntity<>(productVariantService.searchProductVariantsByProductName(productName), HttpStatus.OK);
    }

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
		productVariantService.deleteById(id);
		return new ResponseEntity<>("Product with Id: " + id + " was deleted", HttpStatus.OK);
	}
}

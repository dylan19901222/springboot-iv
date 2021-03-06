package www.com.springboot_iv.api;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import www.com.springboot_iv.component.EmployeeSpecification;
import www.com.springboot_iv.dao.EmployeeDAO;
import www.com.springboot_iv.dto.CreateEmployeeDTO;
import www.com.springboot_iv.dto.UpdateEmployeeDTO;
import www.com.springboot_iv.entity.Employee;

@RestController
public class EmployeeAPI {

	@Autowired
	private EmployeeDAO employeeDao;

	@Autowired
	private EmployeeSpecification employeeSpecification;

	@RequestMapping(value = "/createEmployee", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<Object> createEmployee(@RequestBody CreateEmployeeDTO createEmployeeDTO) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(createEmployeeDTO, employee);
		return new ResponseEntity<>(employeeDao.save(employee), HttpStatus.OK);
	}

	@RequestMapping(value = "/updateEmployee", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<Object> updateEmployee(
			@Validated @RequestBody UpdateEmployeeDTO updateEmployeeDTO) {
		Employee employee = employeeDao.findOne(updateEmployeeDTO.getId());
		Optional<Employee> employeeOptional = Optional.ofNullable(employee);
		if (!employeeOptional.isPresent()) {
			return new ResponseEntity<>("{ \"message\": \"Id is not found\"}", HttpStatus.NOT_FOUND);
		}
		BeanUtils.copyProperties(updateEmployeeDTO, employee);
		return new ResponseEntity<>(employeeDao.save(employee), HttpStatus.OK);
	}

	@RequestMapping(value = "/removeEmployee", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public ResponseEntity<Object> removeEmployee(@RequestParam Integer id) {
		employeeDao.delete(id);
		return new ResponseEntity<>("{ \"message\": \"Remove success\"}", HttpStatus.OK);
	}

	@RequestMapping(value = "/findEmployee", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody ResponseEntity<Object> findEmployee(
			@RequestParam(value = "employeeId", required = false) Integer employeeId,
			@RequestParam(value = "employeeName", required = false) String employeeName,
			@RequestParam(value = "employeeAge", required = false) Integer employeeAge,
			@RequestParam(value = "departmentName", required = false) String departmentName,
			@RequestParam(value = "pageNum", required = false) Integer pageNum) {
		
		Specification<Employee> specification = employeeSpecification
				.queryByIdAndNameAndAgeAndDepartmentName(employeeId, employeeName, employeeAge, departmentName);
		// 判斷pageNum是否為null，如為null給予pageNum初始值1
		Optional<Integer> pageNumOptional = Optional.ofNullable(pageNum);
		pageNum = pageNumOptional.orElse(1);
		Page<Employee> employeePage = employeeDao.findAll(specification, new PageRequest(pageNum - 1, 10));
		return new ResponseEntity<>(employeePage, HttpStatus.OK);
	}
}

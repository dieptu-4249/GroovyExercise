import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.text.DateFormat

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

def json = new File("employees.json")

try {
    def data = new JsonSlurper().parse(json)
	List<Employee> employees = data.collect { 
		new Employee(it.name, it.position, it.office, it.age, Date.parse("dd/MM/yyyy", it.startDate), it.salary)
	}
	// salary of Bradley
	println "=====1====="
	def foundEmployee = employees.find{
		employee -> employee.name.contains('Bradley')
	}
	println "Salary of Bradley: \$${foundEmployee.salary}"
	//people with salary >400
	println "=====2====="
	println "People with salary > \$400"
	List<Employee> employeesWithSalary = employees.findAll { 
		employee -> employee.salary > 400
	}
	for(Employee e in employeesWithSalary) {
		println "Name: ${e.name} | Salary: \$${e.salary}"
	}
	// first 3 guys with office in Tokyo
	println "=====3====="
	println "3 people with office in Tokyo"
	List<Employee> employeesWithOffice = employees.findAll { 
		employee -> employee.office == "Tokyo"
	}
	employeesWithOffice = employeesWithOffice.take(3);
	for(Employee e in employeesWithOffice) {
		println "Name: ${e.name} | Office: ${e.office}"
	}
	// people < 40 years old
	println "=====4====="
	println "People with age <= 40"
	List<Employee> employeesWithAge = employees.findAll{
		employee -> employee.age < 40 || employee.age == 40
	}
	for(Employee e in employeesWithOffice) {
		println "Name: ${e.name} | Age: ${e.age}"
	}
	//people with number 3 in their age
	println "=====5====="
	println "People with 3 in their age"
	List<Employee> employeesWith3InAge = employees.findAll{
		employee -> employee.age.toString().contains('3')
	}
	for(Employee e in employeesWith3InAge) {
		println "Name: ${e.name} | Age: ${e.age}"
		
	}
	//people with start date from 1/1/2011 onwards
	println "=====6====="
	println "People with start date from 1/1/2011"
	Date cutoff = Date.parse("dd/MM/yyyy", "1/1/2011")
	List<Employee> employeesWithStartDate = employees.findAll { 
		employee -> employee.startDate >= cutoff
	}
	for(Employee e in employeesWithStartDate) {
		println "Name: ${e.name} | Start Date: ${e.startDate.getDateString()}"
	}
	// People with position as Accountant or Software Engineer
	//and salary < 5 million VND (take the exchange rate from the sheet)
	println "=====7====="
	println "---People with position as Accountant or Software Engineer and salary < 5 milion VND"
	TestData rateData = findTestData("Data Files/Exchange Rate Data");
	int rate = rateData.getValue(2,1).toInteger();
	List<Employee> employeeWithPosAndSalary = employees.findAll{
		employee -> (employee.position == 'Account' || employee.position == 'Software Engineer') && (employee.salary*rate < 5000000)
	}
	for(Employee e in employeeWithPosAndSalary) {
		println "Name: ${e.name} | Exchange salary: ${e.salary*rate} VND"
	}
	// write data to CSV file
	println "=====8====="
	println "Writing data to CSV"

	def headers = ["Name", "Position", "Office", "Age", "StartDate", "Salary"]
	def csv = new StringBuilder()
	//header
	csv.append(headers.join(",")).append("\n")
	//data
	employees.each { employee ->
	    csv.append([
	        employee.name,
	        employee.position,
	        employee.office,
	        employee.age,
	        employee.startDate.format("dd/MM/yyyy"),
	        employee.salary
	    ].join(",")).append("\n")
	}

	new File("employees.csv").text = csv.toString()

	println "CSV file created successfully!"
	
	
	
	
	
} catch (Exception e) {
    println "Error processing JSON file: ${e.getMessage()}"
}
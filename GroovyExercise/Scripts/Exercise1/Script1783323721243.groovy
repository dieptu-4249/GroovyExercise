import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import internal.GlobalVariable
import io.swagger.util.Json

import org.openqa.selenium.Keys as Keys

TestData testData = findTestData("Data Files/Employee Excel Data")
// get intput data set from Excel file 
List<Employee> employees = []
for(int row=1; row<=57; row++) {
	employees.add(new Employee(testData.getValue(1, row),
												 testData.getValue(2, row),
												 testData.getValue(3, row),
												 testData.getValue(4, row).toInteger(),
												 Date.parse("MM/dd/yyyy",testData.getValue(5, row)) , 
												 testData.getValue(6, row).replace('$', '').toInteger()));
}
// q1: salary of Bradley
Employee foundEmployee = employees.find { it.name.contains('Bradley') };
println "=========="
println "--- Bradley salary is: \$${foundEmployee.salary} ---";
// q2: people with salary > $400
println "=========="
println "--- People with salary > 400:---";
List<Employee> employeesWithHighSalary = employees.findAll { 
	employee -> employee.salary > 400;
};
for(Employee e in employeesWithHighSalary) {
	println "Name: ${e.name} | Salary: \$${e.salary}";
}
// q3: First 3 people with office in Tokyo
println "=========="
println "---The first 3 people with office at Tokyo---"
List<Employee> employeesWithOffice = employees.findAll { 
	employee -> employee.office == 'Tokyo';
}.take(3);
for(Employee e in employeesWithOffice) {
	println "Name: ${e.name} | Office: ${e.office}";
}
// q4: people < 40 years old
println "=========="
println "---People with age < 40---"
List<Employee> employeesWithAge = employees.findAll{
	employee -> employee.age < 40
}
for(Employee e in employeesWithAge) {
	println "Name: ${e.name} | Age: ${e.age}"
}
// people with the number 3 in their age
println "=========="
println "---People with the number 3 in their age---"
List<Employee> employeesWith3InTheirAges = employees.findAll { 
	employee -> employee.age.toString().contains('3');
}
for(Employee e in employeesWith3InTheirAges) {
	println "Name: ${e.name} | Age: ${e.age}";
}
// people start date from 1/1/2011
println "=========="
println "---People with start date from 1/1/2011 onwards---"
Date cutoff = Date.parse("MM/dd/yyyy","2011/1/1");
List<Employee> employeesWithStartDate = employees.findAll { 
	employee -> employee.startDate >= cutoff;
}
for(Employee e in employeesWithStartDate) {
	println "${e.name} | ${e.startDate.getDateString()}";
}
// People with position as Accountant or Software Engineer 
//and salary < 5 million VND (take the exchange rate from the sheet)
println "=========="
println "---People with position as Accountant or Software Engineer and salary < 5 milion VND"
TestData rateData = findTestData("Data Files/Exchange Rate Data");
int rate = rateData.getValue(2,1).toInteger();
List<Employee> employeeWithPosAndSalary = employees.findAll{
	employee -> (employee.position == 'Accountant' || employee.position == 'Software Engineer') && (employee.salary*rate < 5000000)
}
for(Employee e in employeeWithPosAndSalary) {
	println "Name: ${e.name} | Exchange salary: ${e.salary*rate} VND"
}
// write all data to JSON file
def employeeMaps = employees.collect { 
	[
		name: it.name,
		position: it.position,
		office: it.office,
		age: it.age,
		startDate: it.startDate.format("dd/MM/yyyy"),
		salary: it.salary
		]
}
String jsonOutput = JsonOutput.prettyPrint(JsonOutput.toJson(employeeMaps))
new File("employees.json").text = jsonOutput;
def rateMap = [
	USD: rateData.getValue("USD", 1).toInteger(),
	VND: rateData.getValue("VND", 1).toInteger()
]

String rateJson = JsonOutput.prettyPrint(
	JsonOutput.toJson(rateMap)
)

new File("rate.json").text = rateJson


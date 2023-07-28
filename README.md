# Data Mapper - Java Library for JSON and XML Parsing

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Data Mapper is a powerful Java library that offers a comprehensive solution for parsing, transforming, and mapping JSON or XML files to Java beans, driven by a given set of rules. It is designed to simplify the process of data manipulation and conversion in your Java projects.
<br><br> With the latest version, Data Mapper introduces a new feature that allows seamless transformation of one Java bean to another type. Now, you can effortlessly convert your Java beans between different classes, making it even more versatile and adaptable to various data processing scenarios.
1. [ Features ](#features)
2. [ Installation ](#installation)
3. [ Methods Exposed ](#methods)
4. [ Operations Supported ](#operations)
5. [ Input File Structures ](#inputoutput)
6. [ Contact ](#contact)


<a name="features"></a>
## Features
- Effortlessly parse JSON and XML files into Java beans.
- Seamlessly transform JSON and XML strings into Java beans.
- Preview the transformed JSON or XML as a string.
- Flexibly transform one Java bean type into another using specified rules.
- Transform data using custom rules to match specific requirements.
- Exceptionally fast and lightweight for optimal performance.

<a name="installation"></a>
## Installation

To use Data Mapper in your project, include the following dependency:

```xml
<dependency>
    <groupId>org.perfios</groupId>
    <artifactId>data-mapper</artifactId>
    <version>1.1.1</version>
</dependency>
```

<a name="methods"></a>
## Methods exposed
### 1. `transformFile`
Transforms a JSON or XML file according to specified rules and returns an instance of the desired class type. <br>The overloaded version of this method accepts JSON or XML along with the rules as `File` type to perform the same.
#### Prameters expected
<table style="border: none; width: 100%; padding: 0; margin: 0;">
  <tr>
    <td><b><i>inputPath</i></b> <code>String</code><br> The path to the input file (JSON or XML) to be transformed.</td>
    <td><b><i>rulesPath</i></b> <code>String</code><br> The path to the rules file containing transformation rules.</td>
    <td rowspan="2"><b><i>className</i></b> <code>Class&lt;T&gt;</code><br> The desired class type to be returned after transformation.</td>
  </tr>
  <tr>
    <td><b><i>input</i></b> <code>File</code><br> The input JSON or XML file to be transformed.</td>
    <td><b><i>rules</i></b> <code>File</code><br> The rules file containing transformation rules.</td>
  </tr>
</table>

#### Returns
An instance of the desired class type, representing the transformed data.

#### Usage
```java
import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

public class Usage
{
	public static void main(String[] args) {
		// Create an instance of the DataMapper interface
        DataMapper dataMapper = new DataMapperImpl();

        // Specify the paths to the input JSON file and the rules file
        String inputPath = "/path/to/input.json";
        String rulesPath = "/path/to/rules.txt";
        
        // Define the desired Java bean class (Employee in this case)
        Class<Employee> desiredClass = Employee.class;

        // Transform the JSON file using the specified rules and class type
        Employee transformedEmployee = dataMapper.transformFile(inputPath, rulesPath, desiredClass);
	}
}
```

### 2. `transformString`
Transforms a JSON or XML string according to specified rules and returns an instance of the desired class type.
#### Prameters expected
<table style="border: none; width: 100%; padding: 0; margin: 0;">
  <tr>
    <td><b><i>inputString</i></b> <code>String</code> <br> The JSON or XML string to be transformed.</td>
    <td><b><i>rulesString</i></b> <code>String</code><br> The string containing transformation rules.</td>
    <td><b><i>className</i></b> <code>Class&lt;T&gt;</code><br> The desired class type to be returned after transformation.</td>
  </tr>
</table>


#### Returns
An instance of the desired class type, representing the transformed data.

#### Usage
```java
import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

public class Usage
{
	public static void main(String[] args) {
		// Create an instance of the DataMapper interface
        DataMapper dataMapper = new DataMapperImpl();

        // Specify the paths to the input JSON file and the rules file
        String inputPath = "/path/to/input.json";
        String rulesPath = "/path/to/rules.txt";
        
        // Read the contents of the input JSON file and rules file into strings
        String inputString = new String(Files.readAllBytes(Paths.get(inputPath.getAbsolutePath())));
        String rulesString = new String(Files.readAllBytes(Paths.get(rulesPath.getAbsolutePath())));
       
        
        // Define the desired Java bean class (Employee in this case)
        Class<Employee> desiredClass = Employee.class;

        // Transform the JSON file using the specified rules and class type
        Employee transformedEmployee = dataMapper.transformString(inputString, rulesString, desiredClass);
	}
}
```

### 3. `transformBean`
Transforms an input Java Bean according to specified rules and returns another Java bean of the desired type. <br>The overloaded version of this method accepts the transformation rules as `File` type to perform the same.
#### Prameters expected
<table style="border: none; width: 100%; padding: 0; margin: 0;">
  <tr>
    <td rowspan="2"><b><i>inputBean</i></b> <code>Object</code><br>The input Java bean to be transformed.</td>
    <td><b><i>rulesString</i></b> <code>String</code><br> The rules string containing transformation rules.</td>
    <td rowspan="2"><b><i>className</i></b> <code>Class&lt;T&gt;</code><br> The desired class type to be returned after transformation.</td>
  </tr>
  <tr>
    <td><b><i>rules</i></b> <code>File</code><br> The rules file containing transformation rules.</td>
  </tr>
</table>

#### Returns
An instance of the desired class type, representing the transformed data.

#### Usage
```java
import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

public class Usage
{
	public static void main(String[] args) {
		// Create an instance of the DataMapper interface
        DataMapper dataMapper = new DataMapperImpl();

        // Specify the paths to the input Java bean and the rules file
        EmployeeTypeA inputBean = new EmployeeTypeA("emp_id","emp_name","emp_salary");
        File rules= new File("/path/to/rules.txt");
        
        // Define the desired Java bean class (Employee in this case)
        Class<EmployeeTypeB> desiredClass = EmployeeTypeB.class;

        // Transform the JSON file using the specified rules and class type
        EmployeeTypeB transformedEmployee = dataMapper.transformFile(inputBean, rules, desiredClass);
	}
}
```

### 4. `getTransformedString`
Transforms an input JSON or XML string according to specified rules and returns a string representing the transformed input in the desired extension format. <br>The overloaded version of this method accepts JSON or XML along with the rules as `File` type to perform the same.
#### Prameters expected
<table style="border: none; width: 100%; padding: 0; margin: 0;">
  <tr>
    <td><b><i>input</i></b> <code>String</code><br>The input JSON or XML string to be transformed.</td>
    <td><b><i>rules</i></b> <code>String</code><br> The rules string containing transformation rules.</td>
    <td rowspan="2"><b><i>ext</i></b> <code>DataMapperImpl.Extension</code><br> The extension indicating the format of the input file (JSON or XML).</td>
  </tr>
  <tr>
    <td><b><i>input</i></b> <code>File</code><br>The input JSON or XML file to be transformed.</td>
    <td><b><i>rules</i></b> <code>File</code><br> The rules file containing transformation rules.</td>
  </tr>
</table>

#### Returns
A string representing the transformed JSON or XML input in the desired extension format.

#### Usage
```java
import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

public class Usage
{
	public static void main(String[] args) {
		// Create an instance of the DataMapper interface
        DataMapper dataMapper = new DataMapperImpl();

        //Create File objects of input file and rules file using their respective paths
        File input = new File("/path/to/input.json");
        File rules = new File("/path/to/rules.txt");

        // Transform the JSON file using the specified rules and desired output type
        String transformedEmployee = dataMapper.getTransformedString(input, rules, DataMapperImpl.Extension.XML);
	}
}
```
<a name="operations"></a>
## Operations Supported
<table>
	<tr>
		<td><code>#add</code></td>
		<td>"Facilitates addition of numbers or concatenation of two input fields."</td>
	</tr>
	<tr>
		<td><code>#sub</code></td>
		<td>Facilitates subtraction of numbers between two fields from input file</td>
	</tr>
	<tr>
		<td><code>#mul</code></td>
		<td>Facilitates multiplication of numbers between two fields from input file</td>
	</tr>
	<tr>
		<td><code>#div</code></td>
		<td>Facilitates division of numbers between two fields from input file</td>
	</tr>
	<tr>
		<td><code>#default</code></td>
		<td>Facilitates setting default values for fields.</td>
	</tr>
</table>

<a name="inputoutput"></a>
## Example Input File Structures and Output
### JSON or XML input
<table style="border: none; width: 100%; padding: 0; margin: 0;">
<tr>
	<td>Example Input JSON File  </td>
	<td>Example Input XML File</td>
</tr>
<tr>
<td>

```json
{
  "details": {			
    "name": {
      "firstName": "John",
      "lastName": "Doe"
    },
    "addresses": [
      {
        "address1": {
          "phoneNumber": 1234567890,
          "state": "California"
        }
      },
      {
        "address2": {
          "phoneNumber": 9876543210,
          "state": "New York"
        }
      }
    ]
  }
}

```
</td>
<td>

```xml
<root>
    <details>
        <name>
            <firstName>Jane</firstName>
            <lastName>Doe</lastName>
	</name>
	<addresses>
	    <address1>
	        <phoneNumber>5555555555</phoneNumber>
		<state>Texas</state>
	    </address1>
	</addresses>
	<addresses>
	    <address2>
		<phoneNumber>1234567890</phoneNumber>
		<state>Washington</state>
	    </address2>
	</addresses>
    </details>
</root>

```
 
</td>	
</tr>
	
</table>

### Example Rules File:
<i>rules.txt</i>
<table width="100%">
	<tr>
		<td><b>PersonalInfo/name</b> = details/name/firstName #add details/name/lastName</td>
	</tr>
	<tr>
		<td><b>PersonalInfo/details</b> = details/addresses[]/address1/state</td>
	</tr>
	<tr>
		<td><b>PersonalInfo/age</b> = #default 25</td>
	</tr>
</table>

### Output Java bean structure
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonalInfo {
    String name;
    String age;
    Object details;
}
```
<a name="contact"></a>
## Contact
If you have any questions or feedback, feel free to reach out to us via [e-mail](mailto:nithinsudarsan740@gmail.com) .

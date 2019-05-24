This is a group project for cs6310 Spring 2019 Assignment 6

Team: A6-6
Team members: Jing Xue, Yanqun Xu, Xicheng Huang, Lu Yu

Files to submit: 
- design_docs: UML sequence, class, deployment and use case diagrams 
- Source code(zip): including all java source files
- Jar folder(zip): osmowsis_source.jar, test cases and log files 
- demo video link: https://youtu.be/91ntmTd2_1c
- ova link: https://drive.google.com/open?id=1am0LbBQA7JMBkMITGZ6A6_WQq77f4QWv
   Overview: 0:00
   Configuration: 1:40
   GUI: 2:00
   Small lawn demo: 3: 18
   Error handle : 5:49 
   Large lawn demo: 6:39
   Log: 7:41 

Notes for installation:
1) Run app in command line mode (Your operation system):
Unzip the jar file and go to the directory in the termimal
run cmd in current directory:
$ java -jar osmowsis_source.jar <test cases> 
The test case files are in the same working directory as the JAR file
You will see the UI in one second, adjust the frame size as needed.
For best resolution, set the display scale at 100%. 
Go ahead to play with the simulation.
A log file will be created finally in the same folder 

2) Run app in command line mode(VM):
import VM 
Open your terminal 
Go to desktop/cs6310
Go to the directory of jar folder
run with the cmd
$ java -jar osmowsis_source.jar <test cases> 
The test case files are in the same working directory as the JAR file
You will see the UI in one second, adjust the frame size as needed.
A log file will be created finally in the same folder 

3) Run app in an ide (IntelliJ)
Create a new project by importing osmowsis_souce to IDE
Edit configuration: click "Edit configurations" under the run area.
In the Main class field, specify the class that contains the main() method. 
Type the working program arguments: the path of one of the test cases
Click OK, or press Enter when ready.
Click apply and ok
Right-click Main class, then click "Run"
A log file will be created in the end


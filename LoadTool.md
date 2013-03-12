##Load Tool ##

LoadTool is running a load test for the system.It can automatically obtain a api key ,so don't need to config that.

Method 1:Running .bat/.sh command:
    Run LoadTool.sh/LoadTool.bat to start.
    Runs the specified script in multiple threads at the specified frequency. Press Ctrl+C to end.

Method 2:Running ant command:
    Run ant LoadTool to start.
    Runs the specified script in multiple threads at the specified frequency. delete loadTestRunning.txt file to end.


### Command Line Options ###
  -frequency <arg>    Max Frequency per thread (e.g. 100 per second) -1 is max possible.
  -help               print script usage.
  -host <arg>         Host address.
  -hostPort <arg>     Host Port.
  -numThreads <arg>   Number of concurrent Threads.
  -reportFile <arg>   File to write report to. Otherwise will write to screen.
  -script <arg>       Test script to run.
  -verbose            More verbose output.   


### Simple Usage ###
Windows:
1.Running load test with ant command:
    Open windows cmd.
    Open amplafi-flow-client directory.
    Running ant LoadTool.
    Then you should see the prompts below:
        input-runargs:
        [input] Type the desired command line arguments (next time use -Dargs=...)
    Then enter the args like this(other args are optional,these are requried)(the args's value is not fixed):
        -host http://sandbox.farreach.es -hostPort 8080 -numThreads 8 -script test.groovy


2.Getting the test stop:
    Open amplafi-flow-client directory in the folder.
    Find a file named LOAD_TOOL_RUNNING and delete it.

3.Check the report to see how many calls the server can handle every second:
    if you didn't set the reportFile,the report is on the screen of run test windows,you can check it there.
    else you can check it in the reportFile.


// premature optimisation is the root of all evil
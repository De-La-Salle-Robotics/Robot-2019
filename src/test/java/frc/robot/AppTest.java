package frc.robot;

import frc.robot.Robot;

import org.junit.*;
import static org.junit.Assert.*;

public class AppTest {

    private class TimedTests implements Runnable {
        private final long TIMEOUT = 5000;

        private Robot _robot;
        private long _startTime;
        private boolean _started;
        private boolean _ended;
        private boolean _exceptionOccured;
        public TimedTests(Robot robot)
        {
            _robot = robot;
            _started = false;
            _ended = false;
            _exceptionOccured = false;
        }
        public void run()
        {
            _started = true;
            _startTime = System.currentTimeMillis();

            try
            {
                _robot.robotInit();
                _robot.robotPeriodic();
                _robot.autonomousInit();
                _robot.autonomousPeriodic();
                _robot.teleopPeriodic();
                _robot.testPeriodic();
                _ended = true;
            } 
            catch(Exception ex)
            {
                _exceptionOccured = true;
            }
        }

        public boolean outOfTime()
        {
            if(!_started) return false;
            return _startTime + TIMEOUT < System.currentTimeMillis(); /* return true if we started more than 5 seconds ago */
        }

        public boolean finished()
        {
            return _ended;
        }

        public String errorMessage()
        {
            if(_exceptionOccured) {
                return "An exception occured while testing robot function";
            }
            if(_ended) {
                return "No error occured, we finished";
            }
            /* Assume we ran out of time if nothing else happened */
            return "We ran out of time while testing robot function";
        }
    }

    @Test
    public void TestAllFunctionsReturn()
    {
        TimedTests timedTests = new TimedTests(new Robot());

        new Thread(timedTests).start();

        while(!timedTests.outOfTime() && !timedTests.finished())
        {
            try
            {
                Thread.sleep(100, 0);
            }
            catch(Exception ex) { }
        }
        if(!timedTests.finished())
        {
            /* We ran out of time, fail the test */
            fail(timedTests.errorMessage());
        }
    }
}
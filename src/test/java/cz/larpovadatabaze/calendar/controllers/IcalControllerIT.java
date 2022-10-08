package cz.larpovadatabaze.calendar.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.larpovadatabaze.RootTestWithDbConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = RootTestWithDbConfig.class
)
public class IcalControllerIT {
    @Test
    void getHello() {
        
    }
}

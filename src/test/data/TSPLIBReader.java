package test.data;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import data.InstanceReader;
import main.Configuration;

public class TSPLIBReader {
    @Test
    public void test() {
        InstanceReader instanceReader = new InstanceReader(Configuration.instance.dataFilePath);
        instanceReader.open();
        data.TSPLIBReader tspLibReader = new data.TSPLIBReader(instanceReader);
        assertEquals(280,tspLibReader.getCities().size());
    }
}
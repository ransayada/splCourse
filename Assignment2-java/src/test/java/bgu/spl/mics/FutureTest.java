package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }


    /*Test flow:
     *Resolve the future, test get to make sure equals to the result we've inserted.
     */
    @Test
    public void testGet() throws InterruptedException{
        assertFalse(future.isDone());
        String str = "someResult";
        future.resolve(str);
        try{
            assertTrue(str.equals((future.get())));
        }
        catch (InterruptedException e){}
    }

    /*Test flow:
     *Make sure isDone is false
     *Resolve the future, expected result: IsDone == true, get's result will be equals to str;
     */
    @Test
    public void testResolve() {
        assertFalse(future.isDone());
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        try{
            assertTrue(str.equals((future.get())));
        }
        catch (InterruptedException e){}
    }

    /*Test flow:
     *Make sure isDone is false
     *Resolve the future, expected result: IsDone == true.
     */
    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
    }

    /*Test flow:
     *Check get for unresolved future file. Expect - null after time is done;
     *Resolve the future, test get to make sure equals to the result we've inserted.
     */
    @Test
    public void testGetWithTimeOut() throws InterruptedException {
        {
            assertFalse(future.isDone());
            try{
                assertNull(future.get(100, TimeUnit.MILLISECONDS));
            }
            catch (InterruptedException e){}
            future.resolve("foo");
            try{
                assertEquals("foo", future.get(100, TimeUnit.MILLISECONDS));
            }
            catch (InterruptedException e){}
        }
    }
}

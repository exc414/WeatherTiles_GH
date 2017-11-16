package io.bluephoenix.weathertiles.core.data.remote;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import java8.util.function.Supplier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Gets weather api information from the web using retrofit. Usable with a
 * completable future since it implements Supplier.
 * @author Carlos A. Perez Zubizarreta
 */
public class GetWeatherInfo<V> implements Callback<V>, Supplier<Events.APIResponse<V>>
{
    private Events.APIResponse<V> responseBack;
    private CountDownLatch countDownLatch;
    private int retries = 0;
    private int maxRetryAttempts = 3;

    public GetWeatherInfo(Call<V> retrofitCallType)
    {
        retrofitCallType.enqueue(this);
        countDownLatch = new CountDownLatch(1);
        responseBack = new Events().new APIResponse<>();
    }

    /**
     * @return an API response depending on the type of call the was passed.
     */
    @Override
    public Events.APIResponse<V> get()
    {
        try { countDownLatch.await(); }
        catch(InterruptedException e) { e.printStackTrace(); }
        return responseBack;
    }

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure
     * such as a 404 or 500.
     * Call {@link Response#isSuccessful()} to determine if the response
     * indicates success.
     *
     * @param call A retrofit2 call object.
     * @param response A retrofit2 Response with the payload from the API.
     */
    @Override
    public void onResponse(Call<V> call, Response<V> response)
    {
        responseBack.setResponse(response.body());
        responseBack.setHasFailed(false);
        //Once a response is received and set, then let the future return
        //the response using the get() method.
        countDownLatch.countDown();
    }

    /**
     * Invoked when a network exception occurred talking to the server or
     * when an unexpected exception occurred creating the request or processing
     * the response.
     *
     * @param call A retrofit2 call object.
     * @param throwable Throwable object containing information about the failure.
     */
    @Override
    public void onFailure(Call<V> call, Throwable throwable)
    {
        if(throwable instanceof UnknownHostException
                || throwable instanceof SocketTimeoutException)
        {
            responseBack.setHasFailed(true);
            countDownLatch.countDown();
        }
        else
        {
            //If request failed with a different exception retry the call.
            if(retries < maxRetryAttempts) { retry(call); retries++; }
            else
            {
                responseBack.setHasFailed(true);
                countDownLatch.countDown();
            }
        }
    }

    /**
     * Use the call from the onFailure method to re-initiated the call.
     * @param call A retrofit2 call object.
     */
    private void retry(Call<V> call)
    {
        call.clone().enqueue(this);
    }
}

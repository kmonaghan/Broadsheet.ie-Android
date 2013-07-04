package ie.broadsheet.app.services;

import android.app.Application;

import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.googlehttpclient.json.JacksonObjectPersisterFactory;

public class BroadsheetServices extends GoogleHttpClientSpiceService {

    @Override
    public CacheManager createCacheManager(Application application) {
        CacheManager cacheManager = new CacheManager();

        JacksonObjectPersisterFactory jacksonObjectPersisterFactory;
        try {
            jacksonObjectPersisterFactory = new JacksonObjectPersisterFactory(application);
            cacheManager.addPersister(jacksonObjectPersisterFactory);
        } catch (CacheCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cacheManager;
    }

}

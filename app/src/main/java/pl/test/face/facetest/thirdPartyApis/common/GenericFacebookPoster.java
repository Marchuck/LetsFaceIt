package pl.test.face.facetest.thirdPartyApis.common;

import android.support.annotation.NonNull;

import pl.test.face.facetest.ReactivePoster;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class GenericFacebookPoster {
    public static final String FRONT = "New item ! \n";

    public interface ToString {
        String toString();
    }

    public static <T extends ToString> Observable<Boolean> postLater(@NonNull Observable<T> obso) {
        return obso.flatMap(new Func1<T, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(T t) {
                return ReactivePoster.postMessage(FRONT + t.toString());
            }
        });
    }


}

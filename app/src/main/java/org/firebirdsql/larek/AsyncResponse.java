package org.firebirdsql.larek;

import java.util.ArrayList;

/**
 * Created by nazar.humeniuk on 09.06.17.
 */

public interface AsyncResponse {
    void processFinish(ArrayList<String> output);
    void processProductSI(ArrayList<ProductSI> output);
    void processProductII(ArrayList<ProductII> output);
}

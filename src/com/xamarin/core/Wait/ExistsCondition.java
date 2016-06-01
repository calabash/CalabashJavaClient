package com.xamarin.core.Wait;

import com.xamarin.core.Elements.ElementList;
import com.xamarin.core.Elements.Existable;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Created by chrisf on 6/1/16.
 */
public class ExistsCondition extends Condition {
    private Existable exister;
    private boolean shouldExist = true;

    public ExistsCondition(@NonNull Existable exister, boolean shouldExist) {
        super();
        this.exister = exister;
        this.shouldExist = shouldExist;
    }

    public ExistsCondition(@NonNull Existable exister) {
        this(exister, true);
    }

    @Override
    public boolean check() {
        return exister.exists() ? shouldExist : !shouldExist;
    }
}

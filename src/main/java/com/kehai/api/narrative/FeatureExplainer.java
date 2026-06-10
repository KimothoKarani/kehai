package com.kehai.api.narrative;

import com.kehai.api.scoring.features.FeatureVector;

public interface FeatureExplainer {

    /**
     * The contributions-map key this explainer corresponds to.
     */

    String featureKey();

    /**
     * @return a FeatureExplanation if this feature is in risk territory for
     *          the given customer; null if the feature is not worth mentioning.
     */

    FeatureExplanation explain(FeatureVector vector);
}

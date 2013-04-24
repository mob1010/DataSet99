package de.unijena.bioinf.FragmentationTreeConstruction.computation.filtering;

import de.unijena.bioinf.FragmentationTreeConstruction.model.ProcessedInput;

/**
 * PostProcessors are applied after normalization and decomposition
 */
public interface PostProcessor {

    /**
     *
     */
    public enum Stage {

        AFTER_NORMALIZING, AFTER_MERGING, AFTER_DECOMPOSING

    }


    public ProcessedInput process(ProcessedInput input);

    public Stage getStage();

}

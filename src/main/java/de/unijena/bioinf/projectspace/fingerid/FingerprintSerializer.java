package de.unijena.bioinf.projectspace.fingerid;

import de.unijena.bioinf.ChemistryBase.fp.ProbabilityFingerprint;
import de.unijena.bioinf.fingerid.FingerprintResult;
import de.unijena.bioinf.projectspace.ComponentSerializer;
import de.unijena.bioinf.projectspace.FormulaResultId;
import de.unijena.bioinf.projectspace.ProjectReader;
import de.unijena.bioinf.projectspace.ProjectWriter;
import de.unijena.bioinf.projectspace.sirius.FormulaResult;

import java.io.IOException;
import java.util.Optional;

public class FingerprintSerializer implements ComponentSerializer<FormulaResultId, FormulaResult, FingerprintResult> {

    @Override
    public FingerprintResult read(ProjectReader reader, FormulaResultId id, FormulaResult container) throws IOException {
        String loc = FingerIdLocations.FingerprintDir + "/" + id.fileName("fpt");
        if (!reader.exists(loc)) return null;
        final CSIClientData csiClientData = reader.getProjectSpaceProperty(CSIClientData.class).orElseThrow();
        final double[] probabilities = reader.doubleVector(loc);
        return new FingerprintResult(new ProbabilityFingerprint(csiClientData.getFingerprintVersion(), probabilities));
    }

    @Override
    public void write(ProjectWriter writer, FormulaResultId id, FormulaResult container, Optional<FingerprintResult> optPrint) throws IOException {
        final FingerprintResult fingerprintResult = optPrint.orElseThrow(() -> new IllegalArgumentException("Could not find finderprint to write for ID: " + id));
        writer.inDirectory(FingerIdLocations.FingerprintDir, ()->{
            writer.doubleVector(id.fileName("fpt"), fingerprintResult.fingerprint.toProbabilityArray());
            return true;
        });
    }

    @Override
    public void delete(ProjectWriter writer, FormulaResultId id) throws IOException {
        writer.delete(FingerIdLocations.FingerprintDir + "/" + id.fileName("fpt"));
    }
}

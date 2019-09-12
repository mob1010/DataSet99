package de.unijena.bioinf.projectspace.fingerid;

import de.unijena.bioinf.ChemistryBase.fp.ClassyFireFingerprintVersion;
import de.unijena.bioinf.ChemistryBase.fp.ClassyfireProperty;
import de.unijena.bioinf.ChemistryBase.fp.MaskedFingerprintVersion;
import de.unijena.bioinf.ChemistryBase.fp.PredictionPerformance;
import de.unijena.bioinf.projectspace.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class CanopusClientSerializer  implements ComponentSerializer<ProjectSpaceContainerId, ProjectSpaceContainer<ProjectSpaceContainerId>, CanopusClientData> {


    @Override
    public CanopusClientData read(ProjectReader reader, ProjectSpaceContainerId id, ProjectSpaceContainer<ProjectSpaceContainerId> container) throws IOException {
        final ArrayList<PredictionPerformance> performances = new ArrayList<>();
        final ClassyFireFingerprintVersion V = ClassyFireFingerprintVersion.getDefault();
        final MaskedFingerprintVersion.Builder builder = MaskedFingerprintVersion.buildMaskFor(V);
        builder.disableAll();
        reader.table("canopus.csv", true, (row)->{
            final int abs = Integer.parseInt(row[1]);
            builder.enable(abs);
        });
        return new CanopusClientData(
                builder.toMask()
        );
    }

    @Override
    public void write(ProjectWriter writer, ProjectSpaceContainerId id, ProjectSpaceContainer<ProjectSpaceContainerId> container, Optional<CanopusClientData> optClientData) throws IOException {
        final CanopusClientData canopusClientData = optClientData.orElseThrow(() -> new IllegalArgumentException("Could not find CanopusClientData to write for ID: " + id));

        final String[] header = new String[]{"relativeIndex", "absoluteIndex", "id", "name", "parentId", "description"};
        final String[] row = header.clone();
        writer.table("csi_fingerid.csv", header, Arrays.stream(canopusClientData.getFingerprintVersion().allowedIndizes()).mapToObj(absoluteIndex -> {
            final ClassyfireProperty property = (ClassyfireProperty) canopusClientData.getFingerprintVersion().getMolecularProperty(absoluteIndex);
            final int relativeIndex = canopusClientData.getFingerprintVersion().getRelativeIndexOf(absoluteIndex);
            row[0] = String.valueOf(relativeIndex);
            row[1] = String.valueOf(absoluteIndex);
            row[2] = property.getChemontIdentifier();
            row[3] = property.getName();
            row[4] = property.getParent().getChemontIdentifier();
            row[5] = property.getDescription();
            return row;
        })::iterator);
    }

    @Override
    public void delete(ProjectWriter writer, ProjectSpaceContainerId id) throws IOException {
        writer.delete("canopus.csv");
    }
}

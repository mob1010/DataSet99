package de.unijena.bioinf.ChemistryBase.ms;

import de.unijena.bioinf.ChemistryBase.fp.*;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FPTest {

    private static class PseudoProperty extends MolecularProperty {
        private int index;
        private PseudoProperty(int index) {
            this.index = index;
        }
        @Override
        public String getDescription() {
            return String.valueOf(index);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PseudoProperty that = (PseudoProperty) o;

            return index == that.index;

        }

        @Override
        public int hashCode() {
            return index;
        }
    }

    protected static final class TestVersion extends FingerprintVersion {

        private final int size;
        public TestVersion(int size) {
            this.size=size;
        }

        @Override
        public MolecularProperty getMolecularProperty(int index) {
            return new PseudoProperty(index);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean compatible(FingerprintVersion fingerprintVersion) {
            return fingerprintVersion.equals(this);
        }
    }

    @Test
    public void testIterators() {
        final TestVersion tv = new TestVersion(10);
        BooleanFingerprint l1, r1;
        ProbabilityFingerprint l2,r2;
        ArrayFingerprint l3,r3;
        {
            // F1={2,5,8}, inv(F1) = {0,1,3,4,6,7,9}
            // F2={5,7,9}, inv(F2) = {0,1,2,3,4,6,8}
            final boolean[] a = new boolean[]{false, false, true, false, false, true, false, false, true, false};
            // F1 u F2 = {2,5,7,8,9}
            // F1 n F2 = {5}

            final boolean[] b = new boolean[]{false, false, false, false, false, true, false, true, false, true};
            l1 = new BooleanFingerprint(tv, a);
            r1 = new BooleanFingerprint(tv,b);
            l2 = l1.asProbabilistic();
            r2 = r1.asProbabilistic();
            l3 = l1.asArray();
            r3 = r1.asArray();
        }

        final TIntArrayList indizes = new TIntArrayList();
        final ArrayList<Boolean> values = new ArrayList<>();
        for (FPIter iter : l1) {
            if (iter.isSet()) indizes.add(iter.getIndex());
        }
        assertArrayEquals("The fingerprints are {2,5,8} ", new int[]{2,5,8}, indizes.toArray());
        indizes.clear();

        for (FPIter iter : r1) {
            if (iter.isSet()) indizes.add(iter.getIndex());
        }
        assertArrayEquals("The fingerprints are {5,7,9} ", new int[]{5,7,9}, indizes.toArray());
        indizes.clear();

        for (AbstractFingerprint fp : new AbstractFingerprint[]{l2, l3}) {
            for (FPIter iter : fp.asDeterministic().asBooleans()) {
                if (iter.isSet()) indizes.add(iter.getIndex());
            }
            assertArrayEquals("Converting the fingerprints does not change their content ", new int[]{2,5,8}, indizes.toArray());
            indizes.clear();
        }

        for (AbstractFingerprint fp : new AbstractFingerprint[]{l1, l2, l3}) {
            for (FPIter iter : fp.asDeterministic().asArray()) {
                if (iter.isSet()) indizes.add(iter.getIndex());
            }
            assertArrayEquals("Converting the fingerprints does not change their content ", new int[]{2,5,8}, indizes.toArray());
            indizes.clear();
        }

        // test just-one iterators

        for (AbstractFingerprint fp : new AbstractFingerprint[]{l1, l2, l3}) {
            for (FPIter iter : fp.presentFingerprints()) {
                indizes.add(iter.getIndex());
            }
            assertArrayEquals(fp.getClass().getSimpleName() + ": iterates over all ones", new int[]{2,5,8}, indizes.toArray());
            indizes.clear();
        }

        // pairwise iterators

        // UNION
        for (FPIter2 union : l1.foreachUnion(r1)) {
            indizes.add(union.getIndex());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{2,5,7,8,9}, indizes.toArray());
        indizes.clear();

        for (FPIter2 union : l2.foreachUnion(r2)) {
            indizes.add(union.getIndex());
        }
        assertArrayEquals("union (probability) takes union of all indizes", new int[]{2,5,7,8,9}, indizes.toArray());
        indizes.clear();

        for (FPIter2 union : l3.foreachUnion(r3)) {
            indizes.add(union.getIndex());
        }
        assertArrayEquals("union (indizes) takes union of all indizes", new int[]{2,5,7,8,9}, indizes.toArray());
        indizes.clear();

        // INTERSECTION

        for (FPIter2 intersection : l1.foreachIntersection(r1)) {
            indizes.add(intersection.getIndex());
            assertEquals(intersection.getIndex(), 5);
        }
        assertArrayEquals("intersection (boolean) takes intersection of all indizes", new int[]{5}, indizes.toArray());
        indizes.clear();

        for (FPIter2 intersection : l2.foreachIntersection(r2)) {
            indizes.add(intersection.getIndex());
            assertEquals(intersection.getIndex(), 5);
        }
        assertArrayEquals("intersection (probability) takes intersection of all indizes", new int[]{5}, indizes.toArray());
        indizes.clear();

        for (FPIter2 intersection : l3.foreachIntersection(r3)) {
            indizes.add(intersection.getIndex());
            assertEquals(intersection.getIndex(), 5);
        }
        assertArrayEquals("intersection (indizes) takes intersection of all indizes", new int[]{5}, indizes.toArray());
        indizes.clear();

        final Boolean[] unionAry = new Boolean[]{true,false,true,true,false,true,true,false,false,true};
        final ArrayList<Boolean> is = new ArrayList<>();
        for (FPIter2 union : l1.foreachUnion(r1)) {
            is.add(union.isLeftSet());
            is.add(union.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", unionAry, is.toArray());
        is.clear();

        for (FPIter2 union : l2.foreachUnion(r2)) {
            is.add(union.isLeftSet());
            is.add(union.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", unionAry, is.toArray());
        is.clear();

        for (FPIter2 union : l3.foreachUnion(r3)) {
            is.add(union.isLeftSet());
            is.add(union.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", unionAry, is.toArray());
        is.clear();

        //////////////////
        // test pairwise
        //////////////////
        // F1={2,5,8}, inv(F1) = {0,1,3,4,6,7,9}
        // F2={5,7,9}, inv(F2) = {0,1,2,3,4,6,8}

        final Boolean[] expectedValues = new Boolean[]{
                false,false,    // 0
                false,false,    // 1
                true,false,     // 2
                false,false,    // 3
                false,false,    // 4
                true,true,      // 5
                false,false,    // 6
                false,true,     // 7
                true,false,     // 8
                false,true      // 9
        };

        indizes.clear();
        values.clear();
        for (FPIter2 iter : l1.foreachPair(r1)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());


        indizes.clear();
        values.clear();
        for (FPIter2 iter : l2.foreachPair(r2)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());


        indizes.clear();
        values.clear();
        for (FPIter2 iter : l3.foreachPair(r3)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());


        indizes.clear();
        values.clear();
        for (FPIter2 iter : l1.foreachPair(r2)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());


        indizes.clear();
        values.clear();
        for (FPIter2 iter : l1.foreachPair(r3)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());



        indizes.clear();
        values.clear();
        for (FPIter2 iter : l2.foreachPair(r3)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());

        indizes.clear();
        values.clear();
        for (FPIter2 iter : l2.foreachPair(r1)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());

        indizes.clear();
        values.clear();
        for (FPIter2 iter : l3.foreachPair(r1)) {
            indizes.add(iter.getIndex());
            values.add(iter.isLeftSet());
            values.add(iter.isRightSet());
        }
        assertArrayEquals("union (boolean) takes union of all indizes", new int[]{0,1,2,3,4,5,6,7,8,9}, indizes.toArray());
        assertArrayEquals("union (boolean) takes union of all indizes", expectedValues, values.toArray());


        //////////////////
        // test mask
        //////////////////
        final MaskedFingerprintVersion fm = MaskedFingerprintVersion.buildMaskFor(tv).disableAll().enable(0).enable(2).enable(4).enable(8).toMask();
        indizes.clear();

        for (FPIter2 inter : fm.mask(l1).foreachUnion(fm.mask(r1))) {
            indizes.add(inter.getIndex());
        }
        assertArrayEquals("masked union (indizes) takes union of all masked indizes", new int[]{2,8}, indizes.toArray());
        indizes.clear();

        for (FPIter2 inter : fm.mask(l2).foreachUnion(fm.mask(r2))) {
            indizes.add(inter.getIndex());
        }
        assertArrayEquals("masked union (indizes) takes union of all masked indizes", new int[]{2,8}, indizes.toArray());
        indizes.clear();

        for (FPIter2 inter : fm.mask(l3).foreachUnion(fm.mask(r3))) {
            indizes.add(inter.getIndex());
        }
        assertArrayEquals("masked union (indizes) takes union of all masked indizes", new int[]{2,8}, indizes.toArray());
        indizes.clear();
    }

    @Test
    public void testTanimoto() {
        final short[] list1 = new short[]{0,  2,  5,7,10,   12,15,16,17};
        final short[] list2 = new short[]{0,1,2,3,    10,11,12,   16,17};
        final FingerprintVersion testVersion = new TestVersion(18);
        final Fingerprint a = new ArrayFingerprint(testVersion, list1);
        final Fingerprint b = new ArrayFingerprint(testVersion, list2);
        final Fingerprint[] left = new Fingerprint[]{a,a.asBooleans()};
        final Fingerprint[] right = new Fingerprint[]{b,b.asBooleans()};

        for (Fingerprint l : left) {
            for (Fingerprint r : right) {
                final double tanimoto = l.tanimoto(r);
                assertEquals(l.getClass().getSimpleName() + " <-> " +  r.getClass().getSimpleName() + " tanimoto: ", 6d/12d, tanimoto, 0.005);
            }
        }

        // and now with masking

        final MaskedFingerprintVersion.Builder builder = MaskedFingerprintVersion.buildMaskFor(testVersion);
        builder.enableAll().disable(1).disable(10).disable(15);
        final MaskedFingerprintVersion fm = builder.toMask();
        for (Fingerprint l : left) {
            for (Fingerprint r : right) {
                final double tanimoto = fm.mask(l).tanimoto(fm.mask(r));
                assertEquals("Masked " + l.getClass().getSimpleName() + " <-> " +  r.getClass().getSimpleName() + " tanimoto: ",5d/9d, tanimoto, 0.005);
            }
        }

    }

}

package uk.kihira.gltf.animation;

/**
 * Combines input and output accessors with an interpolation algorithm to define a keyframe graph (but not its target).
 */
public class Sampler {

    /**
     * The index of an accessor containing keyframe input values, e.g., time.
     * That accessor must have componentType `FLOAT`.
     * The values represent time in seconds with `time[0] >= 0.0`, and strictly increasing values, i.e., `time[n + 1] > time[n]`.
     */
    public int input;

    /**
     * Interpolation algorithm.
     */
    public Interpolation interpolation = Interpolation.LINEAR;

    /**
     * The index of an accessor containing keyframe output values.
     * When targeting translation or scale paths, the `accessor.componentType` of the output values must be `FLOAT`.
     * When targeting rotation or morph weights, the `accessor.componentType` of the output values must be `FLOAT` or normalized integer.
     * For weights, each output element stores `SCALAR` values with a count equal to the number of morph targets.
     */
    public int output;

    public enum Interpolation {
        /**
         * The animated values are linearly interpolated between keyframes.
         * When targeting a rotation, spherical linear interpolation (slerp) should be used to interpolate quaternions.
         * The number output of elements must equal the number of input elements.
         */
        LINEAR,
        /**
         * The animated values remain constant to the output of the first keyframe, until the next keyframe.
         * The number of output elements must equal the number of input elements.
         */
        STEP,
        /**
         * The animation's interpolation is computed using a cubic spline with specified tangents.
         * The number of output elements must equal three times the number of input elements.
         * For each input element, the output stores three elements, an in-tangent, a spline vertex, and an out-tangent.
         * There must be at least two keyframes when using this interpolation.
         */
        CUBICSPLINE
    }
}

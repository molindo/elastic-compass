package at.molindo.setlist.util;

public enum DistanceUnit {
	KILOMETERS {

		@Override
		public double convert(final double units, final DistanceUnit other) {
			switch (other) {
			case KILOMETERS:
				return units;

			case MILES:
				return units * 0.621371192d;

			case NAUTICAL_MILES:
				return units * 0.539956803d;
			}
			throw new IllegalArgumentException();
		}

	},
	MILES {

		@Override
		public double convert(final double units, final DistanceUnit other) {
			switch (other) {
			case MILES:
				return units;

			case KILOMETERS:
				return units * 1.609344d;

			case NAUTICAL_MILES:
				return units * 0.868976242d;
			}
			throw new IllegalArgumentException();
		}

	},
	NAUTICAL_MILES {

		@Override
		public double convert(final double units, final DistanceUnit other) {
			switch (other) {
			case NAUTICAL_MILES:
				return units;

			case MILES:
				return units * 1.15077945d;

			case KILOMETERS:
				return units * 1.85200d;
			}
			throw new IllegalArgumentException();
		}

	};

	public abstract double convert(double units, DistanceUnit other);

}

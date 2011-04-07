package at.molindo.setlist.model.venue;

import java.util.Locale;

import at.molindo.setlist.util.VenueUtil;
import at.molindo.util.Country;

public enum CityNameFormat {

	LEGACY {

		@Override
		public String getString(final City c, final Locale l) {
			final StringBuilder build = new StringBuilder();
			build.append(getCityString(c, l));

			// stf: null state code for unknown cities possible

			if (VenueUtil.isStateCodeUsingCountry(c.getCountry()) && c.getStateCode() != null) {
				build.append(DELIM);
				build.append(c.getStateCode());
			}

			if (Country.GB.equals(c.getCountry()) && c.getStateCode() != null) {
				build.append(DELIM);
				build.append(VenueUtil.getUKCountryNameForAbbreviation(c.getStateCode()));
			} else if (c.getCountry() != null && !Country.US.equals(c.getCountry())
					&& !VenueUtil.isCountryOmmitingCountry(c.getCountry())) {
				build.append(DELIM);
				build.append(c.getCountry().getDisplayName(l));
			}
			return build.toString();
		}

	},

	CITY_NAME_ONLY {

		@Override
		public String getString(final City c, final Locale l) {
			return getCityString(c, l);
		}

	},

	MEDIUM {

		@Override
		public String getString(final City c, final Locale l) {

			final StringBuilder build = new StringBuilder();
			build.append(getCityString(c, l));

			// stf: null state code for unknown cities possible

			if (VenueUtil.isStateCodeUsingCountry(c.getCountry()) && c.getStateCode() != null) {
				build.append(DELIM);
				build.append(c.getStateCode());
			}

			if (Country.US.equals(c.getCountry())) {
				build.append(DELIM);
				build.append("USA");
			} else if (Country.GB.equals(c.getCountry()) && c.getStateCode() != null) {
				build.append(DELIM);
				build.append(VenueUtil.getUKCountryNameForAbbreviation(c.getStateCode()));
			} else if (c.getCountry() != null) {
				build.append(DELIM);
				build.append(c.getCountry().getDisplayName(l));
			}
			return build.toString();
		}

	},

	FULL {

		@Override
		public String getString(final City c, final Locale l) {
			final StringBuilder build = new StringBuilder();
			build.append(getCityString(c, l));

			if (VenueUtil.isStateCodeUsingCountry(c.getCountry())) {

				if (c.getStateCode() != null) {
					build.append(DELIM);
					build.append(c.getStateCode());
				}

			} else if (Country.GB.equals(c.getCountry())) {
				build.append(DELIM);
				if (c.getStateCode() != null) {
					build.append(VenueUtil.getUKCountryNameForAbbreviation(c.getStateCode()));
				} else {
					build.append(c.getCountry().getDisplayName(l));
				}

			} else if (c.getStateName() != null) {
				build.append(DELIM);
				build.append(c.getStateName());
			}

			if (c.getCountry() != null && !VenueUtil.isCountryOmmitingCountry(c.getCountry())) {
				build.append(DELIM);
				build.append(c.getCountry().getDisplayName(l));
			}
			return build.toString();
		}

	};

	private static final String DELIM = ", ";

	public abstract String getString(City c, Locale l);

	public String getCityString(final City c, final Locale l) {
		return c.isUnknownCity() ? City.UNKNOWN_CITY_DISPLAY_NAME : c.getName();
	}

}

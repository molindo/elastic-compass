package at.molindo.setlist.util.compass;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.spi.InternalCompassSession;
import org.compass.gps.CompassGpsException;
import org.compass.gps.IndexPlan;
import org.compass.gps.device.support.parallel.AbstractParallelGpsDevice;
import org.compass.gps.device.support.parallel.IndexEntitiesIndexer;
import org.compass.gps.device.support.parallel.IndexEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.molindo.setlist.model.venue.missing.MissingCity;
import at.molindo.setlist.util.CityImportBean;

public class MissingCityParallelGpsDevice extends AbstractParallelGpsDevice {

	private static final Logger log = LoggerFactory.getLogger(MissingCityParallelGpsDevice.class);

	private CityImportBean _cityImportBean;
	private String _geonamesFileName;
	private String _geonamesAliasesFileName;
	private String _geonamesAdmin1CodesName;

	private final IndexEntity _entity = new IndexEntity() {

		@Override
		public String getName() {
			return MissingCity.class.getName();
		}

		@Override
		public String[] getSubIndexes() {
			return new String[] { MissingCity.SEARCH_ALIAS };
		}

	};

	@Override
	public synchronized void index(final IndexPlan indexPlan) throws CompassGpsException {

		/*
		 * that's a bit of a hack but an easy (as to say the only) way to avoid
		 * an exception as compass complains if the IndexEntities to be indexed
		 * are empty (instead of just skipping)
		 */
		if (indexPlan.getTypes() != null
				&& !new HashSet<Class<?>>(Arrays.<Class<?>> asList(indexPlan.getTypes()))
						.contains(MissingCity.class)) {
			log.info("Skipping indexing, as " + MissingCity.class.getSimpleName()
					+ " is not affected");
			return;
		}

		super.index(indexPlan);
	}

	@Override
	protected IndexEntity[] doGetIndexEntities() throws CompassGpsException {

		return new IndexEntity[] { _entity };
	}

	@Override
	protected IndexEntitiesIndexer doGetIndexEntitiesIndexer() {
		return new IndexEntitiesIndexer() {

			@Override
			public void performIndex(final CompassSession session, final IndexEntity[] entities) throws CompassException {
				boolean accepted = false;
				for (int i = 0; i < entities.length && !accepted; i++) {
					if (entities[i].getName().equals(MissingCity.class.getName())) {
						accepted = true;
					}
				}

				if (_geonamesAliasesFileName == null || _geonamesFileName == null
						|| _geonamesAdmin1CodesName == null) {
					log.warn("Can't index geonames due to missing file locations");
					return;
				}

				final File mainFile = new File(_geonamesFileName);
				final File alternateNamesFile = new File(_geonamesAliasesFileName);
				final File admin1CodesFile = new File(_geonamesAdmin1CodesName);

				if (!mainFile.exists() || !alternateNamesFile.exists() || !admin1CodesFile.exists()) {
					log.warn("Can't index geonames, at least one file doesn't exist!");
					return;
				}

				if (accepted) {
					try {
						_cityImportBean
								.parseGeonameFile(mainFile, alternateNamesFile, admin1CodesFile, new CityImportBean.MissingCityCallback() {

									@Override
									public void onMissingCityCreated(final int indexed, final MissingCity mc) {
										session.create(mc);

										if (indexed % 5000 == 0) {
											((InternalCompassSession) session).flush();
											session.evictAll();
											if (log.isInfoEnabled() && indexed % 50000 == 0) {
												log.info("Indexed missing cities: " + indexed);
											}
										}
									}
								});
					} catch (final Exception e) {
						log.warn("Exception while indexing", e);
					}
				}

			}

		};
	}

	public void setCityImportBean(final CityImportBean cityImportBean) {
		_cityImportBean = cityImportBean;
	}

	public void setGeonamesFileLocation(final String geonamesFileName) {
		_geonamesFileName = geonamesFileName;
	}

	public void setGeonamesAliasesFileLocation(final String geonamesAliasesFileName) {
		_geonamesAliasesFileName = geonamesAliasesFileName;
	}

	public void setGeonamesAdmin1CodesLocation(final String geonamesAdmin1CodesName) {
		_geonamesAdmin1CodesName = geonamesAdmin1CodesName;
	}

}

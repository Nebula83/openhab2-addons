/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2013, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */
package org.openhab.binding.heatmiser.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.heatmiser.HeatmiserBindingProvider;
import org.openhab.binding.heatmiser.internal.thermostat.HeatmiserThermostat;
import org.openhab.binding.heatmiser.internal.thermostat.HeatmiserThermostat.Functions;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.Command;
import org.openhab.core.types.TypeParser;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Chris Jackson
 * @since 1.3.0
 */
public class HeatmiserGenericBindingProvider extends AbstractGenericBindingProvider implements HeatmiserBindingProvider {

	/** {@link Pattern} which matches an In-Binding */
	private static final Pattern BINDING_PATTERN = Pattern
			.compile("([0-9]+):([A-Z]+)");

	static final Logger logger = LoggerFactory.getLogger(HeatmiserGenericBindingProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "heatmiser";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		//if (!(item instanceof SwitchItem || item instanceof DimmerItem)) {
		//	throw new BindingConfigParseException("item '" + item.getName()
		//			+ "' is of type '" + item.getClass().getSimpleName()
		//			+ "', only Switch- and DimmerItems are allowed - please check your *.items configuration");
		//}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		if (bindingConfig != null) {
			HeatmiserBindingConfig config = new HeatmiserBindingConfig();

			config.itemType = item.getClass();

			Matcher bindingMatcher = BINDING_PATTERN.matcher(bindingConfig);

			if (!bindingMatcher.matches()) {
				throw new BindingConfigParseException(getBindingType()+
						" binding configuration must consist of two parts [config="+bindingMatcher+"]");
			} else {
				config.address = Integer.parseInt(bindingMatcher.group(1));
				config.function = Functions.valueOf(bindingMatcher.group(2));

				// Check the type for different functions
				switch(config.function) {
					case SETTEMP:
						break;
					case FROSTTEMP:
					case ROOMTEMP:
					case FLOORTEMP:
						if(config.itemType != NumberItem.class && config.itemType != StringItem.class) {
							logger.error("Only Number and String allowed for Heatmiser:{} function", config.function);
							config = null;
						}
						break;
					case WATERSTATE:
					case HEATSTATE:
					case ONOFF:
						if(config.itemType != SwitchItem.class && config.itemType != StringItem.class) {
							logger.error("Only Switch and String allowed for Heatmiser:{} function", config.function);
							config = null;
						}
						break;
					default:
						config = null;
						logger.error("Unknown or unsupported Heatmiser function: {}", bindingConfig);
						break;
				}
			}

			if(config != null) {
				addBindingConfig(item, config);
			}
		} else {
			logger.warn("bindingConfig is NULL (item=" + item + ") -> processing bindingConfig aborted!");
		}
	}
	
	

	/**
	 * @{inheritDoc
	 */
	public List<String> getBindingItemsAtAddress(int address) {
		List<String> bindings = new ArrayList<String>();
		for (String itemName : bindingConfigs.keySet()) {
			HeatmiserBindingConfig itemConfig = (HeatmiserBindingConfig) bindingConfigs.get(itemName);
			if (itemConfig.hasAddress(address)) {
				bindings.add(itemName);
			}
		}
		return bindings;
	}

	public Functions getFunction(String itemName) {
		HeatmiserBindingConfig config = (HeatmiserBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.function : null;
	}
	
	public int getAddress(String itemName) {
		HeatmiserBindingConfig config = (HeatmiserBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.address : -1;
	}
	
	public Class<? extends Item> getItemType(String itemName) {
		HeatmiserBindingConfig config = (HeatmiserBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.itemType : null;
	}

	class HeatmiserBindingConfig implements BindingConfig {
		Class<? extends Item> itemType;
		int address;
		HeatmiserThermostat.Functions function;

		boolean hasAddress(int addr) {
			if(address == addr)
				return true;
			return false;
		}
	}
}

package org.testgen.runtime.valuetracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public class BluePrintUnderProcessRegistration {
	private final Map<Object, List<Consumer<BluePrint>>> register = new HashMap<>();
	private final List<MapEntry> mapEntryRegister = new ArrayList<>();

	public void register(Object value, Consumer<BluePrint> action) {
		if (register.containsKey(value))
			register.get(value).add(action);

		else
			register.put(value, new ArrayList<>(Arrays.asList(action)));

	}
	
	public void register(Object key, Object value, BiConsumer<BluePrint, BluePrint> action) {
		MapEntry mapEntry = new MapEntry(key, value, action);
		mapEntryRegister.add(mapEntry);
	}
	
	void executeActions(Object value, BluePrint bluePrint) {
		List<Consumer<BluePrint>> actions = register.get(value);
		
		if(actions != null) {
			for (Consumer<BluePrint> action : actions) {
				action.accept(bluePrint);
			}
		}
		
		register.remove(value);
		
		for (MapEntry mapEntry : mapEntryRegister) {
			if(mapEntry.key == value) {
				mapEntry.keyBluePrint = bluePrint;
			}
			
			if(mapEntry.value == value) {
				mapEntry.valueBluePrint = bluePrint;
			}
		}
		
		for (int i = 0; i < mapEntryRegister.size(); i++) {
			MapEntry mapEntry = mapEntryRegister.get(i);
			
			if(mapEntry.canActionBeExecuted()) {
				mapEntry.action();
			}
			
			mapEntryRegister.remove(mapEntry);
			
		}
		
		
	}
	
	class MapEntry{
		final Object key;
		final Object value;
		final BiConsumer<BluePrint, BluePrint> action;
		
		BluePrint keyBluePrint;
		BluePrint valueBluePrint;
		
		MapEntry(Object key, Object value, BiConsumer<BluePrint, BluePrint> action){
			this.key = key;
			this.value = value;
			this.action = action;
		}
		
		boolean canActionBeExecuted() {
			return keyBluePrint != null && valueBluePrint != null;
		}
		
		void action() {
			action.accept(keyBluePrint, valueBluePrint);
		}
		
		
	}
}
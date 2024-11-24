package client.main;

public class Test {

	// public static void main(String[] args) {
	/*
	 * public static void main(String[] args) { Player player1 = new Player("Anh",
	 * "Vu", "vua36"); PlayerState state = player1.getPlayerState();
	 * 
	 * System.out.println(
	 * "---------------------------TEST_convertPlayerStateToNetwork------------------------------"
	 * ); System.out.println("ENEMY TURN ordinal "); PlayerState test1 =
	 * PlayerState.ENEMYTURN; System.out.println(test1.toString()); EPlayerGameState
	 * networkttest = test1.convertPlayerStateToNetwork(test1);
	 * System.out.println(networkttest.toString());
	 * System.out.println(networkttest.ordinal());
	 * System.out.println("YOUR TURN ordinal"); PlayerState test2 =
	 * PlayerState.YOURTURN; System.out.println(test2.toString()); EPlayerGameState
	 * networkttest2 = test2.convertPlayerStateToNetwork(test2);
	 * System.out.println(networkttest2.toString());
	 * System.out.println(networkttest2.ordinal()); PlayerState test3 =
	 * PlayerState.LOST; System.out.println("LOST ordinal");
	 * System.out.println(test3.toString()); EPlayerGameState networkttest3 =
	 * test3.convertPlayerStateToNetwork(test3);
	 * System.out.println(networkttest3.toString());
	 * System.out.println(networkttest3.ordinal());
	 * System.out.println("WON ordinal"); PlayerState test4 = PlayerState.WON;
	 * System.out.println(test4.toString()); EPlayerGameState networkttest4 =
	 * test4.convertPlayerStateToNetwork(test4);
	 * System.out.println(networkttest4.toString());
	 * System.out.println(networkttest4.ordinal()); System.out.println(
	 * "---------------------------TEST_convertPlayerStateFromNetwork------------------------------"
	 * ); EPlayerGameState test5 = EPlayerGameState.MustAct;
	 * System.out.println(test5.toString()); PlayerState test6 = null; test6 =
	 * test6.convertPlayerStateFromNetwork(test5);
	 * System.out.println(test6.toString()); System.out.println(test6.ordinal());
	 * 
	 * 
	 * }
	 */

	/*
	 * ClientHalfMap halfMap = new ClientHalfMap(); ClientHalfMapGenerator generator
	 * = new ClientHalfMapGenerator(halfMap.getMap()); ClientHalfMapValidator
	 * validator = new ClientHalfMapValidator(generator.getMap(),
	 * generator.getFort()); validator.validate(); Position test1 = new
	 * Position(7,0); Position test2 = new Position(8,1); Position test3 = new
	 * Position(9,0);
	 * 
	 * //validator.getMap().get(test1).setTerrain(TerrainType.WATER);
	 * //validator.getMap().get(test2).setTerrain(TerrainType.WATER);
	 * //validator.getMap().get(test3).setTerrain(TerrainType.WATER);
	 * 
	 * Position test4 = new Position(5,3); Position test5 = new Position(6,2);
	 * Position test6 = new Position(5,1); Position test7 = new Position(4,2);
	 * System.out.println(validator.toString());
	 * 
	 * //validator.getMap().get(test4).setTerrain(TerrainType.WATER);
	 * //validator.getMap().get(test5).setTerrain(TerrainType.WATER);
	 * //validator.getMap().get(test6).setTerrain(TerrainType.WATER);
	 * //validator.getMap().get(test7).setTerrain(TerrainType.WATER);
	 * 
	 * 
	 * System.out.println(validator.toString());
	 * 
	 * validator.validate(); System.out.println(validator.toString()); long
	 * mountains = validator.getMap().values().stream().filter(c -> c.getTerrain()
	 * == TerrainType.MOUNTAIN) .count(); long water =
	 * validator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.WATER).count(); long grass =
	 * validator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.GRASS).count(); boolean ret = false;
	 * System.out.println(validator.toString());
	 * System.out.println(validator.getFort().toString());
	 * System.out.println(validator.getMap().get(validator.getFort()).getTerrain().
	 * toString());
	 * 
	 * 
	 * ClientHalfMap clientMap = new ClientHalfMap(); ClientHalfMapGenerator
	 * generator = new ClientHalfMapGenerator(clientMap.getMap());
	 * ClientHalfMapValidator validator = new
	 * ClientHalfMapValidator(generator.getMap(), generator.getFort());
	 * 
	 * System.out.println(
	 * "----------------CLIENTMAP_PRINT----------------------------");
	 * System.out.println(generator.toString());
	 * 
	 * Map<Position, ClientMapNode> waterfields =
	 * generator.getMap().entrySet().stream() .filter(c -> c.getValue().getTerrain()
	 * == TerrainType.WATER) .collect(Collectors.toMap(Map.Entry::getKey,
	 * Map.Entry::getValue));
	 * 
	 * System.out.println(
	 * "----------------WATER_BORDER_CHECK----------------------------"); for (var
	 * entry : waterfields.entrySet()) {
	 * System.out.println(entry.getKey().toString() + " " +
	 * entry.getValue().toString()); } System.out.println(
	 * "--------------------------------------------------------------");
	 * System.out.
	 * println("----------------REQUIRED TERRAINS CHECK-----------------------");
	 * long mountains = generator.getMap().values().stream().filter(c ->
	 * c.getTerrain() == TerrainType.MOUNTAIN) .count(); long water =
	 * generator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.WATER).count(); long grass =
	 * generator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.GRASS).count();
	 * 
	 * System.out.println("Number of Mountains: " + mountains);
	 * System.out.println("Number of Water: " + water);
	 * System.out.println("Number of Grass: " + grass); System.out.println(
	 * "--------------------------------------------------------------");
	 * System.out.
	 * println("----------------FLOOD FILL CHECK------------------------------");
	 * Position test1 = new Position(7, 0); Position test2 = new Position(8, 0);
	 * Position test3 = new Position(9, 0); Position test4 = new Position(9, 1);
	 * Position test5 = new Position(9, 2); Position test6 = new Position(8, 2);
	 * Position test7 = new Position(7, 2); Position test8 = new Position(7, 1);
	 * Position test9 = new Position(8, 1);
	 * 
	 * validator.getMap().get(test1).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test2).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test3).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test4).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test5).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test6).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test7).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test8).setTerrain(TerrainType.WATER);
	 * validator.getMap().get(test9).setTerrain(TerrainType.GRASS);
	 * 
	 * Map<Position, ClientMapNode> waterfields2 =
	 * validator.getMap().entrySet().stream() .filter(c -> c.getValue().getTerrain()
	 * == TerrainType.WATER) .collect(Collectors.toMap(Map.Entry::getKey,
	 * Map.Entry::getValue)); for (var entry : waterfields2.entrySet()) {
	 * System.out.println(entry.getKey().toString() + " " +
	 * entry.getValue().toString()); } long mountains2 =
	 * validator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.MOUNTAIN) .count(); long water2 =
	 * validator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.WATER).count(); long grass2 =
	 * validator.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.GRASS).count();
	 * 
	 * System.out.println(validator.toString());
	 * System.out.println("Number of Mountains: " + mountains2);
	 * System.out.println("Number of Water: " + water2);
	 * System.out.println("Number of Grass: " + grass2); System.out.
	 * println("----------------AFTER FLOODFILL------------------------------");
	 * Map<Position, ClientMapNode> waterfields3 =
	 * validator.getMap().entrySet().stream() .filter(c -> c.getValue().getTerrain()
	 * == TerrainType.WATER) .collect(Collectors.toMap(Map.Entry::getKey,
	 * Map.Entry::getValue)); for (var entry : waterfields3.entrySet()) {
	 * System.out.println(entry.getKey().toString() + " " +
	 * entry.getValue().toString()); }
	 * 
	 * System.out.println(generator.getMap().equals(validator.getMap()));
	 * System.out.println(validator.isCorrect());
	 * 
	 * long mountains3 = nodes.stream().filter(c -> c.getTerrain() ==
	 * ETerrain.Mountain).count(); long water3 = nodes.stream().filter(c ->
	 * c.getTerrain() == ETerrain.Water).count(); long grass3 =
	 * nodes.stream().filter(c -> c.getTerrain() == ETerrain.Grass).count();
	 * 
	 * validator.validate();
	 * System.out.println(validator.checkMapBorderWaterFields());
	 * System.out.println(validator.isCorrect()); System.out.println(mountains3);
	 * System.out.println(water3); System.out.println(grass3); System.out.
	 * println("----------------GENERAL CHECK------------------------------");
	 * ClientHalfMap halfMap = new ClientHalfMap(); ClientHalfMapGenerator
	 * generator2 = new ClientHalfMapGenerator(halfMap.getMap());
	 * ClientHalfMapValidator validator2 = new
	 * ClientHalfMapValidator(generator.getMap(), generator2.getFort());
	 * validator2.validate(); System.out.println(validator2.toString());
	 * System.out.println(validator2.isCorrect());
	 * 
	 * 
	 */
	/*
	 * ClientHalfMap clientMap = new ClientHalfMap(); // fort 0,0
	 * ClientHalfMapGenerator generator = new
	 * ClientHalfMapGenerator(clientMap.getMap()); generator.getFort();
	 * 
	 * long mountains = clientMap.getMap().values().stream().filter(c ->
	 * c.getTerrain() == TerrainType.MOUNTAIN).count(); long water =
	 * clientMap.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.WATER).count(); long grass =
	 * clientMap.getMap().values().stream().filter(c -> c.getTerrain() ==
	 * TerrainType.GRASS).count();
	 * 
	 * System.out.println("Number of Mountains: " + mountains);
	 * System.out.println("Number of Water: " + water);
	 * System.out.println("Number of Grass: " + grass);
	 * 
	 * 
	 * 
	 * System.out.println(
	 * "------------------------------------------------------------------");
	 * 
	 * System.out.println(generator.toString());
	 * 
	 * System.out.println(
	 * "------------------------------------------------------------------");
	 * //Set<PlayerHalfMapNode> nodes = generator.convertToNetwork(clientMap);
	 * //ystem.out.println(nodes.toString()); System.out.println(
	 * "---------------------------TEST_convertTerrainToNetwork------------------------------"
	 * ); System.out.println("WATER ordinal "); TerrainType test1 =
	 * TerrainType.WATER; System.out.println(test1.toString()); ETerrain
	 * networkttest = test1.convertTerrainToNetwork(test1);
	 * System.out.println(networkttest.toString());
	 * System.out.println(networkttest.ordinal());
	 * System.out.println("GRASS ordinal"); TerrainType test2 = TerrainType.GRASS;
	 * System.out.println(test2.toString()); ETerrain networkttest2 =
	 * test2.convertTerrainToNetwork(test2);
	 * System.out.println(networkttest2.toString());
	 * System.out.println(networkttest2.ordinal()); TerrainType test3 =
	 * TerrainType.MOUNTAIN; System.out.println("MOUNTAIN ordinal");
	 * System.out.println(test3.toString()); ETerrain networkttest3 =
	 * test3.convertTerrainToNetwork(test3);
	 * System.out.println(networkttest3.toString());
	 * System.out.println(networkttest3.ordinal());
	 */
	// }
}

import TSim.*;
import java.util.concurrent.*;

public class Lab1 {

    public Lab1(int speed1, int speed2) {
        TSimInterface tsi = TSimInterface.getInstance();
        // Private Semaphore section;
        // What part of the tracks is the critical section
        Semaphore NorthStation = new Semaphore(1);
        Semaphore WestRoad = new Semaphore(1);
        Semaphore EastRoad = new Semaphore(1);
        Semaphore NorthSouthRoad = new Semaphore(1);
        Semaphore NorthNorthRoad = new Semaphore(1);
        Semaphore TRoad = new Semaphore(1);

        try {
            tsi.setSpeed(2, speed2);
            tsi.setSpeed(1, speed1);
        }
        catch (CommandException e) {
            e.printStackTrace();    // or only e.getMessage() for the error
            System.exit(1);
        }

        class Train implements Runnable {
            private int id;
            private int speed;
            private int direction;
            private boolean reverse;

            public Train(int id, int speed) {
                this.id = id;
                this.speed = speed;

                // 1 for down, 2 for up
                this.direction = id;
                this.reverse = false;
            }


            public void changeSpeed(int speedarg) {
            	if (!reverse) {
            		try {
            		tsi.setSpeed(id, speedarg);
            		}
            		catch (CommandException e) {
            			e.printStackTrace();
                        System.exit(1);
            		}
            	}
            	else {
            		try {
                        // When changing direction, speed is inversed
                		tsi.setSpeed(id, (speedarg * -1));
                		}
                		catch (CommandException e) {
                			e.printStackTrace();
                            System.exit(1);
                		}
            	}
            }
            
            @Override
            public void run() {
                try {
                    // Trains acquire their respective starting platform semaphore
                    if (id == 1) {
                        NorthNorthRoad.acquire();
                    }
                    if (id == 2) {
                    	NorthStation.acquire();
                    }
                    while (true) {
                        SensorEvent event = tsi.getSensor(id);
                        if (event.getStatus() == 1) {
                            int xcoord = event.getXpos();
                            int ycoord = event.getYpos();
                            if (xcoord == 6 && ycoord == 11) {
                                // Stop train before reaching the single file rail, wait until available, start train again and release the station rail
                                if (direction == 2) {
                                    changeSpeed(0);
                                    WestRoad.acquire();
                                    tsi.setSwitch(3, 11, 1);
                                    changeSpeed(speed);
                                    NorthStation.release();
                                } else {
                                    // Release the single file rail before heading to station
                                    WestRoad.release();
                                }
                            }
                            if (xcoord == 5 && ycoord == 13) {
                                if (direction == 2) {
                                    // From lower bottom station going up
                                    changeSpeed(0);
                                    WestRoad.acquire();
                                    tsi.setSwitch(3, 11, 2);
                                    changeSpeed(speed);
                                } else {
                                    // Release before heading to station
                                    WestRoad.release();
                                }
                            }
                            if (xcoord == 1 && ycoord == 10) {
                                if (direction == 2) {
                                    if (NorthSouthRoad.tryAcquire()) {
                                        tsi.setSwitch(4, 9, 1);
                                    } else {
                                        tsi.setSwitch(4, 9, 2);
                                    }
                                } else {
                                    if (NorthStation.tryAcquire()) {
                                        tsi.setSwitch(3, 11, 1);
                                    } else {
                                       
                                        tsi.setSwitch(3, 11, 2);
                                    }
                                }
                            }
                            if (xcoord == 7 && ycoord == 10) {
                                if (direction == 2) {
                                    WestRoad.release();
                                }

                            }
                            if (xcoord == 7 && ycoord == 9) {
                                if (direction == 2) {
                                    WestRoad.release();
                                }
                                else {
                                    NorthSouthRoad.release();
                                }
                            }
                            if (xcoord == 9 && ycoord == 9) {
                                if (direction == 2) {
                                    changeSpeed(0);
                                    EastRoad.acquire();
                                    tsi.setSwitch(15, 9, 2);
                                    changeSpeed(speed);
                                } else {
                                    changeSpeed(0);
                                    WestRoad.acquire();
                                    tsi.setSwitch(4, 9, 1);
                                    changeSpeed(speed);
                                }

                            }
                            if (xcoord == 9 && ycoord == 10) {
                                if (direction == 2) {
                                    changeSpeed(0);
                                    EastRoad.acquire();
                                    tsi.setSwitch(15, 9, 1);
                                    changeSpeed(speed);
                                } else {
                                    changeSpeed(0);
                                    WestRoad.acquire();
                                    tsi.setSwitch(4, 9, 2);
                                    changeSpeed(speed);
                                }

                            }
                            if (xcoord == 13 && ycoord == 9) {
                                if (direction == 1) {
                                    EastRoad.release();
                                }
                                else {
                                	NorthSouthRoad.release();
                                }

                            }
                            if (xcoord == 13 && ycoord == 10) {
                                if (direction == 1) {
                                    EastRoad.release();
                                }
                            }
                            if (xcoord == 19 && ycoord == 9) {
                                if (direction == 2) {                                
                                    if (NorthNorthRoad.tryAcquire()) {
                                        tsi.setSwitch(17, 7, 2);
                                    } else {
                                       
                                        tsi.setSwitch(17, 7, 1);
                                    }

                                } else {
                                    
                                    if (NorthSouthRoad.tryAcquire()) {
                                        tsi.setSwitch(15, 9, 2);
                                    } else {
                                        tsi.setSwitch(15, 9, 1);
                                    }
                                }

                            }
                            if (xcoord == 15 && ycoord == 7) {
                                if (direction == 2) {
                                    EastRoad.release();
                                }
                                else {
                                	 NorthNorthRoad.release();
                                }
                            }
                            if (xcoord == 15 && ycoord == 8) {
                                if (direction == 2) {
                                    EastRoad.release();
                                }
                            }
                            if (xcoord == 12 && ycoord == 7) {
                                if (direction == 2) {
                                    changeSpeed(0);
                                    TRoad.acquire();
                                    changeSpeed(speed);
                                } else {
                                    changeSpeed(0);
                                    EastRoad.acquire();
                                    tsi.setSwitch(17, 7, 2);
                                    changeSpeed(speed);
                                }
                            }
                            if (xcoord == 12 && ycoord == 8) {
                                if (direction == 2) {
                                    changeSpeed(0);
                                    TRoad.acquire();
                                    changeSpeed(speed);
                                } else {
                                	TRoad.release();
                                    changeSpeed(0);
                                    EastRoad.acquire();
                                    tsi.setSwitch(17, 7, 1);
                                    changeSpeed(speed);
                                }
                            }
                            if (xcoord == 9 && ycoord == 7) {
                                if (direction == 1) {
                                    TRoad.release();
                                }
                            }
                            if (xcoord == 7 && ycoord == 7) {
                                if (direction == 2) {
                                    TRoad.release();
                                }
                            }
                            if (xcoord == 6 && ycoord == 6) {
                                if (direction == 1) {
                                    changeSpeed(0);
                                    TRoad.acquire();
                                    changeSpeed(speed);
                                }
                            }
                            if (xcoord == 8 && ycoord == 5) {
                                if (direction == 2) {
                                    TRoad.release();
                                }
                                if (direction == 1) {
                                    changeSpeed(0);
                                    TRoad.acquire();
                                    changeSpeed(speed);
                                }

                            }
                            if (xcoord == 14 && (ycoord == 5 || ycoord == 3)) {
                                if (direction == 2) {
                                    changeSpeed(0);
                                    direction = 1;
                                    reverse = !reverse;
                                    Thread.sleep(3000);
                                    changeSpeed(speed);

                                }
                            }
                            if (xcoord == 14 && (ycoord == 11 || ycoord == 13)) {
                                if (direction == 1) {
                                    changeSpeed(0);
                                    direction = 2;
                                    reverse = !reverse;
                                    Thread.sleep(3000);
                                    changeSpeed(speed);

                                }
                            }
                        }
                    }
                    }
                catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }

            Train Train1 = new Train(1, speed1);
            Train Train2 = new Train(2, speed2);

            Thread thread1 = new Thread(Train1);
            Thread thread2 = new Thread(Train2);

        thread1.start();
                thread2.start();
        }
    }
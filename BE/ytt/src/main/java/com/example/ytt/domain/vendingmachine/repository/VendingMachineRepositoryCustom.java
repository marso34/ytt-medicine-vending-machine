package com.example.ytt.domain.vendingmachine.repository;

import com.example.ytt.domain.vendingmachine.domain.VendingMachine;
import org.locationtech.jts.geom.Point;

import java.util.List;

public interface VendingMachineRepositoryCustom {

    List<VendingMachine> getVendingMachines(Point location, double distance, String name);

    List<VendingMachine> getVendingMachinesByMedicine(Point location, double distance, Long medicineId);

}

package com.noken29.vrpjobs.solvers

import com.noken29.vrpjobs.model.SplitDeliveryVehicleRoutingProblem
import com.noken29.vrpjobs.utils.ComplexKey
import org.antlr.v4.runtime.misc.Pair

import static com.noken29.vrpjobs.utils.DiscreteDistribution.discreteDistribution

class AcoSolver {
    SplitDeliveryVehicleRoutingProblem sdvrp
    int numAnts
    BigDecimal pheromoneEvaporation
    Map<String, BigDecimal> vParams, cParams
    Pair<BigDecimal, BigDecimal> paramsBounds

    Map<ComplexKey<Integer, Integer>, BigDecimal> vehiclesPheromone = [:], customersPheromone = [:]
    Map<String, BigDecimal> packageStrategyPheromone = [:]

    AcoSolver(SplitDeliveryVehicleRoutingProblem sdvrp,
              int numAnts,
              BigDecimal initPheromone,
              BigDecimal pheromoneEvaporation,
              Map<String, BigDecimal> vParams,
              Map<String, BigDecimal> cParams,
              Pair<BigDecimal, BigDecimal> paramsBounds) {
        this.sdvrp = sdvrp
        this.numAnts = numAnts
        this.pheromoneEvaporation = pheromoneEvaporation
        this.vParams = vParams
        this.cParams = cParams
        this.paramsBounds = paramsBounds
        this.fillPheromone(initPheromone)
    }

    def fillPheromone(BigDecimal initPheromone) {
        for (vehicleIndex in this.sdvrp.vehicles*.index) {
            for (customerIndex in this.sdvrp.graph.customerIndexes) {
                this.vehiclesPheromone[ComplexKey.from(vehicleIndex, customerIndex)] = initPheromone
            }
        }
        this.sdvrp.graph.customerIndexes.forEach {
            this.customersPheromone[ComplexKey.from(this.sdvrp.graph.depot.index, it)] = initPheromone
        }
        for (customer1Index in this.sdvrp.graph.customers) {
            for (customer2Index in this.sdvrp.graph.customers) {
                if (customer1Index != customer2Index)
                    this.customersPheromone[ComplexKey.from(customer1Index, customer2Index)] = initPheromone
            }
        }
        this.packageStrategyPheromone.WEIGHT = initPheromone
        this.packageStrategyPheromone.VOLUME = initPheromone
    }

    def generateSolution() {
        def routes = []
        def totalWeight = this.sdvrp.totalWeight
        def totalVolume = this.sdvrp.totalVolume

        def depot = this.sdvrp.graph.depot
        def distanceMatrix = this.sdvrp.graph.distanceMatrix
        def numCustomers = this.sdvrp.graph.customerIndexes.size()

        def deliveredCustomers = new HashSet()
        def deliveredPackages = new HashSet()

        while (deliveredCustomers.size() != numCustomers) {
            def customers = []
            def pickedPackages = []

            def cProbs = sdvrp.graph.customerIndexes.collect { customerIndex ->
                if (customerIndex in deliveredCustomers)
                    return BigDecimal.ZERO
                return Math.pow(customersPheromone[ComplexKey.from(depot.index, customerIndex)], cParams.pheromone)
                        * Math.pow(1 / distanceMatrix[depot.index][customerIndex], cParams.distance)
            }
            BigDecimal cProbsSum = cProbs.sum()
            cProbs = cProbs.collect {it / cProbsSum}

            int selectedCustomerIndex = discreteDistribution(cProbs)

            def routeDistance = distanceMatrix[depot.index][selectedCustomerIndex]
            def routePickedWeight = 0.0
            def routePickedVolume = 0.0

            double[] vProbs = sdvrp.vehicles.collect { vehicle ->
                Math.pow(vehiclesPheromone[ComplexKey.from(selectedCustomerIndex, vehicle.index)], vParams.pheromone)
                        * Math.pow(1 / vehicle.serviceCost, vParams.serviceCost)
                        * Math.pow(1 / Math.max(this.sdvrp.customerTotalWeight[selectedCustomerIndex] / vehicle.maxWeight, sdvrp.customerTotalVolume[selectedCustomerIndex] / vehicle.maxVolume), vParams.customerDemand)
                        * Math.pow(1 / Math.max(totalWeight / vehicle.maxWeight, totalVolume / vehicle.maxVolume) * ((sdvrp.packages.size() - deliveredPackages.size()) / sdvrp.packages.size()), vParams.totalDemand)
            }
            BigDecimal vProbsSum = vProbs.sum()
            vProbs = vProbs.collect { it / vProbsSum }

            int selectedVehicleIndex = discreteDistribution(vProbs)
            def selectedVehicle = sdvrp.vehicles[selectedVehicleIndex]

            customers.add(selectedCustomerIndex)
            def vehicleFilled = false

            def pcsProbs = [packageStrategyPheromone.WEIGHT, packageStrategyPheromone.VOLUME]
            BigDecimal pcsProbsSum = pcsProbs.sum()
            pcsProbs = pcsProbs.collect { it / pcsProbsSum }

            def selectedPcs = discreteDistribution(pcsProbs) == 0 ? 'WEIGHT' : 'VOLUME'
            List<Package> packages = selectedPcs == 'WEIGHT'
                    ? sdvrp.packagesSortedWeight[selectedCustomerIndex] : sdvrp.packagesSortedVolume[selectedCustomerIndex]

            for (p in packages) {
                if (!deliveredPackages.contains(p)) {
                    if (routePickedWeight + p.weight <= selectedVehicle.maxWeight
                            && routePickedVolume + p.volume <= selectedVehicle.maxVolume) {
                        routePickedWeight += p.weight
                        routePickedVolume += p.volume
                        pickedPackages.add(p)
                        deliveredPackages.add(p)
                    } else {
                        vehicleFilled = true
                    }
                }
            }

            if (!vehicleFilled) {
                deliveredCustomers.add(selectedCustomerIndex)
            }

            while (!vehicleFilled && deliveredCustomers.size() != numCustomers) {
                cProbs = sdvrp.graph.customerIndexes.collect { customerIndex ->
                    if (customerIndex in deliveredCustomers)
                        return BigDecimal.ZERO
                    return Math.pow(customersPheromone[ComplexKey.from(selectedCustomerIndex, customerIndex)], cParams.pheromone)
                                * Math.pow(1 / distanceMatrix[selectedCustomerIndex, customerIndex], cParams.distance)
                                * Math.pow(1 / Math.max(sdvrp.customerTotalWeight[customerIndex] / selectedVehicle.maxWeight,
                                        sdvrp.customerTotalVolume[customerIndex] / selectedVehicle.maxVolume), cParams.demand)
                }
                cProbsSum = cProbs.sum()
                cProbs = cProbs.collect { it / cProbsSum }

                selectedCustomerIndex = discreteDistribution(cProbs)
                def packagesForSelectedCustomer = selectedPcs == 'WEIGHT' ?
                        sdvrp.packagesSortedWeight[selectedCustomerIndex] : sdvrp.packagesSortedVolume[selectedCustomerIndex]

                boolean atLeastOnePackageLoaded = false
                for (p in packagesForSelectedCustomer) {
                    if (!(p in deliveredPackages)) {
                        if (routePickedWeight + p.weight <= selectedVehicle.maxWeight &&
                                routePickedVolume + p.volume <= selectedVehicle.maxVolume) {
                            routePickedWeight += p.weight
                            routePickedVolume += p.volume
                            pickedPackages.add(p)
                            deliveredPackages.add(p)
                            atLeastOnePackageLoaded = true
                        } else {
                            vehicleFilled = true
                        }
                    }
                }

                if (atLeastOnePackageLoaded) {
                    routeDistance += distanceMatrix[customers[-1]][selectedCustomerIndex]
                    customers.add(selectedCustomerIndex)
                }

                if (!vehicleFilled) {
                    deliveredCustomers.add(selectedCustomerIndex)
                }
            }
            routeDistance += distanceMatrix[customers[-1]][depot.index]
            totalWeight -= routePickedWeight
            totalVolume -= routePickedVolume
        }
    }
}
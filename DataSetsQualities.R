source("SciCl.R") 

loadQualities <- function() {
    qualities <- read.csv("data/openml_data_qualities.csv") 
    
    return (qualities) 
} 
  
computeInformationForClusters <- function(p.values, p.function, p.showPlots = FALSE) { 
    options("scipen" = 100, "digits" = 4) 
    groupedEvaluations <- data.frame(rep(NA, length(p.values[[1]]))) 
    for (i in 1:length(p.values)) {
        singleClusterData <- p.values[[i]] 
        singleClusterData <- as.data.frame(apply(X = singleClusterData, 2, p.function)) 
        colnames(singleClusterData) <- paste("Cluster_", i, sep = "") 
        groupedEvaluations <- cbind(groupedEvaluations, singleClusterData) 
        
        if (p.showPlots) {
            singleClusterData <- p.values[[i]] 
            
            plotData <- singleClusterData[ , -ncol(singleClusterData)] 
            for (j in 1:ncol(plotData)) {
                boxplot(plotData[, j], main = paste(colnames(plotData)[j], " in cluster  ", i, sep = "")) 
            } 
        }
        
    }
    groupedEvaluations <- groupedEvaluations[ , -1] 
    return (groupedEvaluations) 
} 
 
getOriginalClusters <- function() { 
    qualities <- loadQualities() 
    data_wide <- spread(data = qualities, key = 'quality', value = 'value')   
    
    # remove features with many empty elements 
    emptyValuesColumns <- c() 
    for (i in 1:ncol(data_wide)) { 
        emptiesInColumn <- sum(is.na(data_wide[, i])) 
        emptyValuesColumns <- c(emptyValuesColumns, emptiesInColumn)  
    } 
    skipColumns <- emptyValuesColumns > (nrow(data_wide) / 2) 
    data_wide <- data_wide[ , !skipColumns] 
    
    # fill missing values 
    imputeData <- imputeEmptyVals(data_wide[, 2:ncol(data_wide)]) 
    
    rownames(imputeData) <- data_wide$dataset 
    
    # clean empty numbers 
    emptyDataSet <- grep("164_molecular-biology_promoters", rownames(imputeData)) 
    imputeData <- imputeData[-emptyDataSet, ] 
    
    if1 <- grep("NumberOfClasses", names(imputeData)) 
    if2 <- grep("NumAttributes", names(imputeData)) 
    if3 <- grep("ClassEntropy", names(imputeData)) 
    if4 <- grep("PercentageOfMissingValues", names(imputeData)) 
    if5 <- grep("DecisionStumpAUC", names(imputeData)) 
    if6<- grep("MeanMeansOfNumericAtts", names(imputeData)) 
    if7 <- grep("MaxNominalAttDistinctValues", names(imputeData)) 
    if8 <- grep("MajorityClassSize", names(imputeData)) 
    if9 <- grep("NumberOfNumericFeatures", names(imputeData)) 
    data_wide <- imputeData[ , c(if1, if2, if3, if4, if5, if6, if7, if8, if9)] 

    cleanData <- FALSE 
    if (cleanData) {
        for (i in 1:9) {
            minValue <- quantile(x = data_wide[, i], probs = 0.01, na.rm = TRUE) 
            maxValue <- quantile(x = data_wide[, i], probs = 0.99, na.rm = TRUE) 
            data_wide <- data_wide[data_wide[, i] >= minValue & data_wide[, i] <= maxValue, ] 
        } 
    }
        
    recomputeFeatures <- FALSE 
    if (recomputeFeatures) {
        # search for the optimal number of clusters 
        for (ocl in 2:9) {
            features_pair <- combn(names(data_wide)[1:ncol(data_wide)], ocl, simplify = FALSE) 
            
            maxSilValue <- 0 
            ti <- 0
            for(pair in features_pair) {
                sbx_ps <- data_wide[,  pair] 
                time.scaled <- scale(sbx_ps)  
                k.max <- 11
                sil <- rep(0, k.max)
                
                # Compute the average silhouette width for 
                # k = 2 to k = 15
                for(i in 2:k.max){ 
                    srv() 
                    km.res <- kmeans(time.scaled, centers = i, nstart = 25, iter.max = 10) 
                    ss <- silhouette(km.res$cluster, dist(time.scaled))     
                    sil[i] <- mean(ss[, 3]) 
                }
                
                maxValue <- max(sil) 
                n <- length(sil) 
                secMaxValue <- sort(sil,partial=n-1)[n-1] 
                
                reduceSize <- TRUE 
                if (reduceSize) { 
                    srv() 
                    sbx.clusters <- kmeans(time.scaled, centers = which.max(sil), nstart = 25, iter.max = 10)$cluster  
                    mainCluster <- sum(table(sbx.clusters) < 50) == 0                 
                } else {
                    mainCluster <- TRUE 
                }
                
                if (maxValue > maxSilValue & which.max(sil) > 1 & (maxValue - secMaxValue) > 0.01 & 
                    mainCluster & maxValue > 0.2) { 
                    maxSilValue <- maxValue
                    
                    bestPair <- pair 
                    
                    print(paste(pair[1:length(pair)], " have value ", maxSilValue,
                                ", the size of the biggest cluster is less than a half ", mainCluster, 
                                sep = "")) 
                    
                    print(table(sbx.clusters)) 
                    
                    # Plot the  average silhouette width
                    plot(1:k.max, sil, type = "b", pch = 19, frame = FALSE, xlab = "Number of clusters k")
                    abline(v = which.max(sil), lty = 2) 
                } 
                
                ti <- ti + 1 
                if (ti %% 5 == 0) {
                    print(paste(ocl, " and iteration ", ti, sep = ""))   
                }
                
            }     
        } 
        imputeData <- imputeData[, bestPair] 
    } else {
        # for two clusters with a high value of connectivity 
        selectedValues <- c(if2, if3, if5, if7, if8) 
    
        # for three clusters with a high value of connectivity 

        # selectedValues <- c(if2, if4, if5, if6, if7, if9) 
        
        imputeData <- imputeData[, selectedValues] 
    } 
    
    cleanData <- as.data.frame(scale(imputeData)) 
    
    # use cluster analysis 
    srv() 
    numDataSets <- drawSilhouette(p.matrix = cleanData, p.k = 50L) 
    
    # get clusters for data sets with kMeans algorithm 
    srv() 
    clusters <- getkMeansClusters(p.matrix = cleanData, p.numClusters = numDataSets) 
    table(clusters) 

    return (clusters) 
} 

additionalProcessing <- function() { 
    clusters <- getOriginalClusters() 
 
    # usage of PCA visualization method   
    pcaPlots(p.matrix = cleanData, p.clusters = clusters) 
       
    # study visualization received with t-SNE method and resistance against noise
    tsnePlot(p.matrix = cleanData, p.names = rownames(cleanData), noiseLevel = 0, 
             p.clusters = clusters, clusterAsFactor = FALSE, addName = TRUE)  
    
    cleanData <- cbind(imputeData, clusters)  
    cleanData <- split(x = cleanData, f = as.factor(cleanData$clusters))  
    commonData <- computeInformationForClusters(p.values = cleanData,p.function = mean, p.showPlots = FALSE) 

    commonData <- commonData[-nrow(commonData), ] 
    
    numOfAtts <- nrow(commonData) 
    for (i in 1: ncol(commonData)) {
        commonData[numOfAtts + 1, i] <- nrow(cleanData[[i]]) 
    } 
    rownames(commonData)[nrow(commonData)] <- "Size"  

    # identify the structure in data points 
    srv() 
    hi <- hopkins(scale(imputeData), n = nrow(imputeData) - 1) 

    # choose data sets for extended analysis 
    firstDataCluster <- imputeData[clusters == 1, ] 
    secondDataCluster <- imputeData[clusters == 2, ] 
    selectedFirstGroupSets <- rownames(getMedoids(p.matrix = firstDataCluster, p.numMedoids = 5)) 
    selectedSecondGroupSets <- rownames(getMedoids(p.matrix = secondDataCluster, p.numMedoids = 5)) 
            
    noisyData <- c("475_analcatdata_germangss", "848_schlvote", 
                   "737_space_ga", "453_analcatdata_bondrate", 
                   "998_analcatdata_bondrate", "1046_mozilla4", "938_sleuth_ex1221", 
                   "1002_kdd_ipums_la_98-small", "836_sleuth_case1102", "735_cpu_small", 
                   "759_analcatdata_olympic2000", "938_sleuth_ex1221", "1002_kdd_ipums_la_98-small",
                   "1018_kdd_ipums_la_99-small", "179_adult", "311_oil_spill", 
                   "761_cpu_act", "993_kdd_ipums_la_97-small", "947_arsenic-male-bladder", 
                   "951_arsenic-male-lung", "950_arsenic-female-lung")       
}
   


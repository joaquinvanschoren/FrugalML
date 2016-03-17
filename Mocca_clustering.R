library(MOCCA)

# perform new calculations or load data from a file 
newCalculatations = FALSE 

if(newCalculatations) { 
    bSize <- dim(hugeMatrix)[1] 
    mocca_res <- mocca(hugeMatrix, R = bSize, K = 2:30, iter.max = 1000, nstart = 10) 
    analyzePareto(mocca_res$objectiveVals) 
} else {
    mocca_res <- readRDS("objects/mocca_res_huge_matrix_complete.rds") 
} 

# save results since it takes substatial amount of time to compute a number of clusters 
saveRDS(mocca_res, file = "objects/mocca_res_huge_matrix_complete.rds") 
 
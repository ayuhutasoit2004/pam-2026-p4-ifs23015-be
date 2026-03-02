package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IPohonRepository
import org.delcom.repositories.PohonRepository
import org.delcom.services.PlantService
import org.delcom.services.PohonService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }

    // Pohon Repository
    single<IPohonRepository> {
        PohonRepository()
    }

    // Pohon Service
    single {
        PohonService(get())
    }
}



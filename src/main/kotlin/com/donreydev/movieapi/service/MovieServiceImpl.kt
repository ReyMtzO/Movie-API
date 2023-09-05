package com.donreydev.movieapi.service

import com.donreydev.movieapi.dto.MovieDTO
import com.donreydev.movieapi.repository.MovieRepository
import com.donreydev.movieapi.utils.exceptions.MovieException
import com.donreydev.movieapi.utils.mapper.MovieMapper
import org.springframework.stereotype.Service

@Service
class MovieServiceImpl(
    private val movieRepository: MovieRepository,
    private val movieMapper: MovieMapper
) : MovieService {
    override fun createMovie(movieDTO: MovieDTO): MovieDTO {

        if (movieDTO.id != -1)
            throw MovieException("Id must be null or -1.")
        // with movie id = 25
        val movie = movieRepository.save(movieMapper.toEntity(movieDTO))
        return movieMapper.fromEntity(movie)
    }

    override fun getMovies(): List<MovieDTO> {
        val movies = movieRepository.getAllMovies()

        if (movies.isEmpty())
            throw MovieException("List of movies is empty.")

        return movies.map {
            movieMapper.fromEntity(it)
        }
    }

    override fun getMovie(id: Int): MovieDTO {
        val optionalMovie = movieRepository.findById(id)
        val movie = optionalMovie.orElseThrow { MovieException("Movie with id $id is not present") }
        return movieMapper.fromEntity(movie)
    }

    override fun updateMovie(movieDTO: MovieDTO): MovieDTO {
        val exist = movieRepository.existsById(movieDTO.id)

        if (!exist)
            throw MovieException("Movie with id ${movieDTO.id} is not present")

        if (movieDTO.rating == 0.0 || movieDTO.name == "Default movie")
            throw MovieException("Complete movie object is expected")

        movieRepository.save(movieMapper.toEntity(movieDTO))
        return movieDTO
    }

    override fun deleteMovie(id: Int) {
        val exist = movieRepository.existsById(id)

        if (!exist)
            throw MovieException("Movie with id $id is not present")

        movieRepository.deleteById(id)
    }
}
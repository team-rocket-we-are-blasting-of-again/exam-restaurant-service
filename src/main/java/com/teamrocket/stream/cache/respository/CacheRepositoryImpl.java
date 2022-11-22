package com.teamrocket.stream.cache.respository;

import org.springframework.stereotype.Service;

import com.teamrocket.stream.cache.JedisFactory;

import redis.clients.jedis.Jedis;

@Service
public class CacheRepositoryImpl implements CacheRepository {

    @Override
    public void putAccessToken(String token, String userId) {

        try (Jedis jedis = JedisFactory.getConnection()) {

            jedis.set(token, userId);

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public String getUserIdByAccessToken(String token) {

        try (Jedis jedis = JedisFactory.getConnection()) {

            return jedis.get(token);

        } catch (Exception e) {
        	e.printStackTrace();
        }

        return null;
    }

    @Override
    public void putActivationCode(String email, String activationCode) {

        try (Jedis jedis = JedisFactory.getConnection()) {

            jedis.hset(email, String.valueOf(activationCode), "");
            jedis.expire(email, 15 * 60);

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    public String queryEmailActivationCode(String email, String code) {

        try (Jedis jedis = JedisFactory.getConnection()) {
            return jedis.hget(email, code);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteAny(String key) {
        try (Jedis jedis = JedisFactory.getConnection()) {
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


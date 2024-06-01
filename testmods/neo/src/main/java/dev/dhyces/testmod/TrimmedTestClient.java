package dev.dhyces.testmod;

import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.testmod.client.TestClientTags;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.TrimmedClientTagApi;
import dev.dhyces.trimmed.api.maps.MapHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Optional;

@Mod(value = TrimmedTest.MODID, dist = Dist.CLIENT)
public class TrimmedTestClient {
    public TrimmedTestClient() {
        NeoForge.EVENT_BUS.addListener(this::loggedIn);
    }

    private void loggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        var t = "minecraft:textures/models/armor/test_layer_1/trimmed_test";
        TrimmedClientTagApi.getInstance().getTag(TestClientTags.TEST_TAG).getSet().forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedClientTagApi.getInstance().getTag(TestClientTags.TEST_ITEM_TAG).getSet().forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedClientTagApi.getInstance().getTag(TestClientTags.TEST_CUSTOM_REG_KEY).getSet().forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
//        TrimmedClientTagApi.getInstance().getTag(TestClientTags.TEST_BIOME_TAG).ifPresent(holders -> holders.forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString())));

        TrimmedClientMapApi.getInstance().getSimpleMap(TestClientMapKeys.MANUAL_TEST_MAP).getMap().forEach((key, value) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", key, value));
        TrimmedTest.LOGGER.info("Map present! " + TrimmedClientMapApi.getInstance().getSimpleMap(TestClientMapKeys.MANUAL_TEST_ITEM_MAP).getMap().get(Items.IRON_INGOT));
//        TrimmedClientMapApi.getInstance().getSimpleMap(TestClientMapKeys.MANUAL_TEST_BIOME_MAP).getMap().forEach((key, value) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", key, value));

//        TrimmedTest.TEST_DELEGATE.forEach((entry) -> {
//            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
//        });
//
//        TrimmedTest.TEST_DELEGATE_2.forEach((entry) -> {
//            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
//        });
    }

    public static final MapHolder<Block, String> DESC_MAP = TrimmedClientMapApi.getInstance().getSimpleMap(TestClientMapKeys.MANUAL_SCANNER_DESCS);

    public static void printDescriptor(Player player, Block block) {
        Optional.ofNullable(DESC_MAP.getMap().get(block)).ifPresent(s -> player.sendSystemMessage(Component.literal(s)));
    }
}